<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>New Contact</title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css">
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js"></script>
    </head>
    <body>
        <div class="container">
            <form role="form" class="form-horizontal" action="/contact/add" method="post">
                        <h3>New contact</h3>
                        <select class="selectpicker form-control form-group" id="uniqueSelectpicker" name="group">
                            <option value="-1">Default</option>
                            <c:forEach items="${groups}" var="group">
                                <option value="${group.id}">${group.name}</option>
                            </c:forEach>
                        </select>
                        <input class="form-control form-group" type="text" name="name" placeholder="Name">
                        <input class="form-control form-group" type="text" name="surname" placeholder="Short description">
                        <input class="form-control form-group" type="text" name="phone" placeholder="Long description">
                        <input class="form-control form-group" type="text" name="email" placeholder="Phone">
                    <input type="submit" class="btn btn-primary" value="Add">
            </form>
            <form action="/contact/add/xml" method="post" enctype="multipart/form-data">
                <input type="file" name="file" accept=".xml"/>
                <button type="submit">Upload XML File</button>
            </form>
        </div>

        <script>
            $('#uniqueSelectpicker').selectpicker();
        </script>
    </body>
</html>