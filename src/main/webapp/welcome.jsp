<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Système de Réservation de Rendez-vous Médicaux</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <style>
        .jumbotron {
            background-color: #f8f9fa;
        }
        .feature-box {
            padding: 20px;
            margin-bottom: 20px;
            border-radius: 5px;
            background-color: #fff;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
    </style>
</head>
<body>
<div class="container">
    <div class="jumbotron mt-4">
        <h1 class="display-4">Bienvenue sur notre système de rendez-vous médicaux</h1>
        <p class="lead">Une solution simple et efficace pour gérer vos rendez-vous médicaux</p>
        <hr class="my-4">
        <p>Connectez-vous ou inscrivez-vous pour commencer à utiliser notre service.</p>
        <a class="btn btn-primary btn-lg mr-2" href="${pageContext.request.contextPath}/login.jsp" role="button">Connexion</a>
        <a class="btn btn-secondary btn-lg" href="${pageContext.request.contextPath}/Register.jsp" role="button">Inscription</a>
    </div>

    <div class="row">
        <div class="col-md-6">
            <div class="feature-box">
                <h3>Pour les Patients</h3>
                <ul>
                    <li>Prenez rendez-vous avec votre médecin en quelques clics</li>
                    <li>Consultez la liste de vos rendez-vous</li>
                    <li>Annulez facilement un rendez-vous si nécessaire</li>
                </ul>
            </div>
        </div>
        <div class="col-md-6">
            <div class="feature-box">
                <h3>Pour les Médecins</h3>
                <ul>
                    <li>Consultez votre planning de rendez-vous</li>
                    <li>Gérez vos disponibilités</li>
                    <li>Annulez ou reportez des rendez-vous si nécessaire</li>
                </ul>
            </div>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>

