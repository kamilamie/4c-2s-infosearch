<#ftl encoding='UTF-8'>
<html>
<head>
    <meta charset="UTF-8">
    <title>Search</title>
</head>
<body>
<div>
    <form method="post" action="/search">
        Enter your request: <input type="text" name="request">
        <input type="submit" value="Submit">
    </form>
    (Please wait, the result will appear in next 5 seconds)
</div>

<#if results?? >
    <div>
        <#list results as result>
            <a href="${result}">${result}</a><br>
        </#list>
    </div>
</#if>

</body>
</html>
