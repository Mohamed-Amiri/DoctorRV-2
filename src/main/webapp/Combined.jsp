<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><c:choose>
        <c:when test="${entity == 'medecin'}">Medical Appointments - Médecins</c:when>
        <c:when test="${entity == 'patient'}">Medical Appointments - Patients</c:when>
        <c:otherwise>Medical Appointments</c:otherwise>
    </c:choose></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<header>
    <div class="container">
        <h1>
            <c:choose>
                <c:when test="${entity == 'medecin'}">Médecins</c:when>
                <c:when test="${entity == 'patient'}">Patients</c:when>
                <c:otherwise>Medical Appointments</c:otherwise>
            </c:choose>
        </h1>
        <nav>
            <ul>
                <li><a href="${pageContext.request.contextPath}/">Home</a></li>
                <c:choose>
                    <c:when test="${entity == 'medecin'}">
                        <li><a href="${pageContext.request.contextPath}/medecins/new">Nouveau Médecin</a></li>
                        <li><a href="${pageContext.request.contextPath}/medecins/search">Rechercher Médecin</a></li>
                    </c:when>
                    <c:when test="${entity == 'patient'}">
                        <li><a href="${pageContext.request.contextPath}/patients/new">Nouveau Patient</a></li>
                    </c:when>
                </c:choose>
            </ul>
        </nav>
    </div>
</header>

<main>
    <div class="container">
        <c:if test="${not empty message}">
            <div class="message success"><c:out value="${message}" /></div>
            <c:remove var="message" scope="session"/>
        </c:if>

        <c:if test="${not empty error}">
            <div class="message error"><c:out value="${error}" /></div>
            <c:remove var="error" scope="session"/>
        </c:if>

        <%-- *** List View *** --%>
        <c:if test="${empty action}">
            <h2>Liste des <c:out value="${entity == 'medecin' ? 'Médecins' : 'Patients'}" /></h2>
            <table>
                <thead>
                <tr>
                    <c:choose>
                        <c:when test="${entity == 'medecin'}">
                            <th>ID</th>
                            <th>Nom</th>
                            <th>Email</th>
                            <th>Téléphone</th>
                            <th>Spécialité</th>
                            <th>Actions</th>
                        </c:when>
                        <c:when test="${entity == 'patient'}">
                            <th>ID</th>
                            <th>Nom</th>
                            <th>Email</th>
                            <th>Téléphone</th>
                            <th>Numéro Assurance</th>
                            <th>Actions</th>
                        </c:when>
                    </c:choose>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="item" items="${entity == 'medecin' ? medecins : patients}">
                    <tr>
                        <c:choose>
                            <c:when test="${entity == 'medecin'}">
                                <td><c:out value="${item.id}" /></td>
                                <td><c:out value="${item.username}" /></td>
                                <td><c:out value="${item.email}" /></td>
                                <td><c:out value="${item.telephone}" /></td>
                                <td><c:out value="${item.specialite}" /></td>
                                <td class="list-actions">
                                    <a href="?entity=medecin&action=view&id=${item.id}">Voir</a>
                                    <a href="?entity=medecin&action=edit&id=${item.id}">Modifier</a>
                                    <form action="${pageContext.request.contextPath}/medecins/" method="post">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="id" value="${item.id}">
                                        <button type="submit" onclick="return confirm('Êtes-vous sûr de vouloir supprimer ce médecin ?')">Supprimer</button>
                                    </form>
                                </td>
                            </c:when>
                            <c:when test="${entity == 'patient'}">
                                <td><c:out value="${item.id}" /></td>
                                <td><c:out value="${item.username}" /></td>
                                <td><c:out value="${item.email}" /></td>
                                <td><c:out value="${item.telephone}" /></td>
                                <td><c:out value="${item.numeroAssurance}" /></td>
                                <td class="list-actions">
                                    <a href="?entity=patient&action=view&id=${item.id}">Voir</a>
                                    <a href="?entity=patient&action=edit&id=${item.id}">Modifier</a>
                                    <form action="${pageContext.request.contextPath}/patients/" method="post">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="id" value="${item.id}">
                                        <button type="submit" onclick="return confirm('Êtes-vous sûr de vouloir supprimer ce patient ?')">Supprimer</button>
                                    </form>
                                </td>
                            </c:when>
                        </c:choose>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:if>

        <%-- *** Form View (New/Edit) *** --%>
        <c:if test="${action == 'new' || action == 'edit'}">
            <h2>${action == 'new' ? 'Nouveau' : 'Modifier'} ${entity}</h2>
            <form action="${pageContext.request.contextPath}/${entity}s/" method="post">
                <input type="hidden" name="action" value="${action == 'new' ? 'create' : 'update'}">
                <c:if test="${action == 'edit'}">
                    <input type="hidden" name="id" value="${param.id}">
                </c:if>

                <label for="nom">Nom:</label><br>
                <input type="text" id="nom" name="nom" value="<c:out value="${entity == 'medecin' ? medecin.username : patient.username}" />" required><br><br>

                <label for="email">Email:</label><br>
                <input type="email" id="email" name="email" value="<c:out value="${entity == 'medecin' ? medecin.email : patient.email}" />" required><br><br>

                <label for="motDePasse">Mot de passe:</label><br>
                <input type="password" id="motDePasse" name="motDePasse"><br><br>
                <small>Laissez vide pour ne pas changer le mot de passe</small><br><br>

                <label for="telephone">Téléphone:</label><br>
                <input type="text" id="telephone" name="telephone" value="<c:out value="${entity == 'medecin' ? medecin.telephone : patient.telephone}" />" required><br><br>

                <c:choose>
                    <c:when test="${entity == 'medecin'}">
                        <label for="specialite">Spécialité:</label><br>
                        <input type="text" id="specialite" name="specialite" value="<c:out value="${medecin.specialite}" />" required><br><br>

                        <label for="numeroProfessionnel">Numéro Professionnel:</label><br>
                        <input type="text" id="numeroProfessionnel" name="numeroProfessionnel" value="<c:out value="${medecin.numeroProfessionnel}" />" required><br><br>

                        <label for="disponibilite">Disponibilité:</label><br>
                        <input type="text" id="disponibilite" name="disponibilite" value="<c:out value="${medecin.disponibilite}" />" required><br><br>
                    </c:when>
                    <c:when test="${entity == 'patient'}">
                        <label for="numeroAssurance">Numéro Assurance:</label><br>
                        <input type="text" id="numeroAssurance" name="numeroAssurance" value="<c:out value="${patient.numeroAssurance}" />" required><br><br>

                        <label for="groupeSanguin">Groupe Sanguin:</label><br>
                        <input type="text" id="groupeSanguin" name="groupeSanguin" value="<c:out value="${patient.groupeSanguin}" /><"><br><br>

                        <label for="antecedentsMedicaux">Antécédents Médicaux:</label><br>
                        <textarea id="antecedentsMedicaux" name="antecedentsMedicaux"><c:out value="${patient.antecedentsMedicaux}" /></textarea><br><br>
                    </c:when>
                </c:choose>

                <button type="submit">${action == 'new' ? 'Créer' : 'Modifier'}</button>
                <a href="?entity=${entity}" class="button">Annuler</a>
            </form>
        </c:if>

        <%-- *** View View *** --%>
        <c:if test="${action == 'view'}">
            <h2>Détails du ${entity}</h2>
            <c:choose>
                <c:when test="${entity == 'medecin'}">
                    <p><strong>ID:</strong> <c:out value="${medecin.id}" /></p>
                    <p><strong>Nom:</strong> <c:out value="${medecin.username}" /></p>
                    <p><strong>Email:</strong> <c:out value="${medecin.email}" /></p>
                    <p><strong>Téléphone:</strong> <c:out value="${medecin.telephone}" /></p>
                    <p><strong>Spécialité:</strong> <c:out value="${medecin.specialite}" /></p>
                    <p><strong>Numéro Professionnel:</strong> <c:out value="${medecin.numeroProfessionnel}" /></p>
                    <p><strong>Disponibilité:</strong> <c:out value="${medecin.disponibilite}" /></p>
                </c:when>
                <c:when test="${entity == 'patient'}">
                    <p><strong>ID:</strong> <c:out value="${patient.id}" /></p>
                    <p><strong>Nom:</strong> <c:out value="${patient.username}" /></p>
                    <p><strong>Email:</strong> <c:out value="${patient.email}" /></p>
                    <p><strong>Téléphone:</strong> <c:out value="${patient.telephone}" /></p>
                    <p><strong>Numéro Assurance:</strong> <c:out value="${patient.numeroAssurance}" /></p>
                    <p><strong>Groupe Sanguin:</strong> <c:out value="${patient.groupeSanguin}" /></p>
                    <p><strong>Antécédents Médicaux:</strong> <c:out value="${patient.antecedentsMedicaux}" /></p>
                </c:when>
            </c:choose>

            <div class="mt-20">
                <a href="?entity=${entity}&action=edit&id=${param.id}" class="button">Modifier</a>
                <a href="?entity=${entity}" class="button">Retour à la liste</a>
            </div>
        </c:if>

        <%-- *** Search View (Medecin Only) *** --%>
        <c:if test="${entity == 'medecin' && action == 'search'}">
            <h2>Recherche de Médecins par Spécialité</h2>
            <form action="${pageContext.request.contextPath}/medecins/search" method="get">
                <label for="specialite">Spécialité:</label>
                <input type="text" id="specialite" name="specialite" value="<c:out value="${specialite}" />">
                <button type="submit">Rechercher</button>
            </form>

            <c:if test="${not empty medecins}">
                <h3>Résultats de la recherche pour la spécialité: <c:out value="${specialite}" /></h3>
                <table>
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nom</th>
                        <th>Email</th>
                        <th>Téléphone</th>
                        <th>Spécialité</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="medecin" items="${medecins}">
                        <tr>
                            <td><c:out value="${medecin.id}" /></td>
                            <td><c:out value="${medecin.username}" /></td>
                            <td><c:out value="${medecin.email}" /></td>
                            <td><c:out value="${medecin.telephone}" /></td>
                            <td><c:out value="${medecin.specialite}" /></td>
                            <td><a href="?entity=medecin&action=view&id=${medecin.id}">Voir</a></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:if>

            <c:if test="${empty medecins && not empty specialite}">
                <p>Aucun médecin trouvé pour la spécialité: <c:out value="${specialite}" /></p>
            </c:if>
            <a href="?entity=medecin" class="button">Retour à la liste des médecins</a>
        </c:if>

    </div>
</main>

<footer>
    <div class="container">
        <p>Medical Appointments Application</p>
    </div>
</footer>

</body>
</html>