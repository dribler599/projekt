<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>

<table border="1">
    <thead>
    <tr>
        <th>jméno</th>
        <th>datum narození</th>
        <th>adresa</th>
        <th>email</th>
        <th>telefonní číslo</th>
    </tr>
    </thead>
    <c:forEach items="${CUSTOMER}" var="customer">
        <tr>
            <td><c:out value="${customer.name}"/></td>
            <td><c:out value="${customer.dateOfBirth}"/></td>
            <td><c:out value="${customer.address}"/></td>
            <td><c:out value="${customer.email}"/></td>
            <td><c:out value="${customer.phoneNumber}"/></td>
            <td><form method="post" action="${pageContext.request.contextPath}/customers/delete?ID=${customer.id}"
                      style="margin-bottom: 0;"><input type="submit" value="Smazat"></form></td>
        </tr>
    </c:forEach>
</table>

<h2>Zadejte knihu</h2>
<c:if test="${not empty chyba}">
    <div style="border: solid 1px red; background-color: yellow; padding: 10px">
        <c:out value="${chyba}"/>
    </div>
</c:if>
<form action="${pageContext.request.contextPath}/customers/add" method="post">
    <table>
        <tr>
            <th>jméno zákazníka:</th>
            <td><input type="text" name="NAME" value="<c:out value='${param.name}'/>"/></td>
        </tr>
        <tr>
            <th>datum narození:</th>
            <td><input type="text" name="DATEOFBIRTH" value="<c:out value='${param.dateOfBirth}'/>"/></td>
        </tr>
        <tr>
            <th>adresa:</th>
            <td><input type="text" name="ADDRESS" value="<c:out value='${param.address}'/>"/></td>
        </tr>
        <tr>
            <th>email:</th>
            <td><input type="text" name="EMAIL" value="<c:out value='${param.email}'/>"/></td>
        </tr>
        <tr>
            <th>telefonni cislo:</th>
            <td><input type="text" name="PHONENUMBER" value="<c:out value='${param.phoneNumber}'/>"/></td>
        </tr>
    </table>
    <input type="Submit" value="Zadat" />
</form>

</body>
</html>