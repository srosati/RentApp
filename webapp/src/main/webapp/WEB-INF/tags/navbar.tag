<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:url value="/" var="marketplace"/>
<c:url value="/create-article" var="createArticle"/>
<html>
<body>

<nav class="navbar shadow-sm navbar-expand-lg bg-color-primary navbar-dark py-3 nav-bar-style mb-3">
    <div class="container-fluid nav-bar">

        <a class="navbar-brand mb-1" href="${marketplace}">
            <img src="<c:url value="/resources/image/rentapp-logo.png"/>" height="50px" alt="RentApp">
        </a>
        <button class=" navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNavAltMarkup"
                aria-controls="navbarNavAltMarkup" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse show">
            <div class="navbar-nav">
                <a class="nav-link active mx-1" aria-current="page" href="${createArticle}">Publish Article</a>
                <a class="nav-link active mx-1" aria-current="page" href="${marketplace}">Marketplace</a>
            </div>
            <div class="navbar-nav ml-auto">
                <a class="nav-link active mx-1" aria-current="page" href="#">LogIn</a>
                <a class="nav-link active mx-1" aria-current="page" href="#">SignUp</a>
            </div>
        </div>
    </div>
</nav>
</body>
</html>