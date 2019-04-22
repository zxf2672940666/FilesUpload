<%--
  Created by IntelliJ IDEA.
  User: ZXF
  Date: 2019/4/21
  Time: 12:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>

    <form action="uploadServlet" method="post" enctype="multipart/form-data">

        File:<input type="file" name="file"/>
        <br><br>
        Desc:<input type="text" name="desc"/>
        <br><<br>
        <input type="submit" value="Submit"/>

    </form>

</body>
</html>
