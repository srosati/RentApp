import { Row } from "react-bootstrap";
import { Review as ReviewT } from "../features/api/reviews/types";
import { strings } from "../i18n/i18n";
import Review from "./Review";

function ReviewList(reviews: ReviewT[]) {
  // const { reviews } = props;
  console.log(reviews);
  return (
    <div>
      <Row>
        {/* TODO: Can review
         <c:if test="${canReview}">
          <div className="col-4">
            <control:linkButton
              href="${writeReview}"
              labelCode="article.createReview.title"
              color="bg-color-action color-grey"
            /> 
          </div>
        </c:if>
        */}
      </Row>
      {reviews.length === 0 ? (
        <p className="lead">{strings.collection.review.noReviews}</p>
      ) : (
        <div>
          {reviews.map((review, i) => (
            <div>
              <Review key={i} {...review}></Review>
              <hr />
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default ReviewList;
