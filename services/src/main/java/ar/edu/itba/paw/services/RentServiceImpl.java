package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.RequestsGetter;
import ar.edu.itba.paw.interfaces.dao.RentDao;
import ar.edu.itba.paw.interfaces.service.EmailService;
import ar.edu.itba.paw.interfaces.service.RentService;
import ar.edu.itba.paw.models.RentProposal;
import ar.edu.itba.paw.models.RentState;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.exceptions.CannotEditRequestException;
import ar.edu.itba.paw.models.exceptions.RentProposalNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class RentServiceImpl implements RentService {

    @Autowired
    private RentDao rentDao;

    @Autowired
    private EmailService emailService;

    private List<RentProposal> getRequests(RequestsGetter getter, long accountId, int state, Long limit, long page) {
        return getter.get(accountId, state, limit, page);
    }

    @Override
    @Transactional
    public List<RentProposal> ownerRequests(long ownerId, int state, Long limit, long page) {
        return getRequests(rentDao::ownerRequests, ownerId, state, limit, page);
    }

    @Override
    @Transactional
    public List<RentProposal> sentRequests(long ownerId, int state, Long limit, long page) {
        List<RentProposal> toReturn = getRequests(rentDao::sentRequests, ownerId, state, limit, page);
        if (state != RentState.PENDING.ordinal()) {
            toReturn.forEach(proposal -> {
                if (!proposal.getSeen()) {
                    proposal.setSeen(true);
                    proposal.setMarked(true);
                } else {
                    proposal.setMarked(false);
                }
            });
        }
        return toReturn;
    }

    @Override
    @Transactional(readOnly = true)
    public long getReceivedMaxPage(long ownerId, int state, Long limit) {
        return rentDao.getReceivedMaxPage(ownerId, state, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public long getSentMaxPage(long ownerId, int state, Long limit) {
        return rentDao.getSentMaxPage(ownerId, state, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RentProposal> findById(long id) {
        return rentDao.findById(id);
    }

    @Override
    @Transactional
    public RentProposal create(String message, LocalDate startDate, LocalDate endDate, long articleId,
                               long renterId, String webpageUrl, Locale locale) {
        RentProposal proposal = rentDao.create(message, RentState.PENDING, startDate, endDate, articleId, renterId);
        emailService.sendMailRequest(proposal, proposal.getArticle().getOwner(), webpageUrl, locale);

        return proposal;
    }

    @Override
    @Transactional
    public void setRequestState(long requestId, RentState state, String webpageUrl, Locale locale) {
        if (state.getIsPending())
            throw new CannotEditRequestException();

        if (state.getIsAccepted())
            acceptRequest(requestId, webpageUrl, locale);
        else
            rejectRequest(requestId, webpageUrl, locale);
    }

    private void acceptRequest(long requestId, String webpageUrl, Locale locale) {
        RentProposal rentProposal = rentDao.findById(requestId).orElseThrow(RentProposalNotFoundException::new);
        rentProposal.setState(RentState.ACCEPTED);

        emailService.sendMailRequestConfirmation(rentProposal, rentProposal.getArticle().getOwner(), webpageUrl, locale);
    }

    private void rejectRequest(long requestId, String webpageUrl, Locale locale) {
        RentProposal rentProposal = rentDao.findById(requestId).orElseThrow(RentProposalNotFoundException::new);
        rentProposal.setState(RentState.DECLINED);

        emailService.sendMailRequestDenied(rentProposal, rentProposal.getRenter(), webpageUrl, locale);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasRented(User renter, long articleId) {
        if (renter == null)
            return false;

        return rentDao.hasRented(renter.getId(), articleId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPresentSameDate(long renterId, long articleId, LocalDate startDate, LocalDate endDate) {
        return rentDao.isPresentSameDate(renterId, articleId, startDate, endDate);
    }
}
