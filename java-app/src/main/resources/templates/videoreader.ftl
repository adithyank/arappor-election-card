<html>

<head>
<meta charset="utf-8"/>
    <link href="https://fonts.googleapis.com/css?family=Open+Sans:300,300i,400,400i,600,600i,700,700i|Roboto:300,300i,400,400i,500,500i,600,600i,700,700i|Poppins:300,300i,400,400i,500,500i,600,600i,700,700i" rel="stylesheet">

    <style>

    body {
        font-family: 'Roboto', 'sans-serif';
    }

        table {
          /*font-family: Arial, Helvetica, sans-serif;*/
          border-collapse: collapse;
          width: 100%;
          border: 2px solid black;
        }

        table td, table th {
          border: 1px solid #ddd;
          padding: 8px;
            width: 50%;
        }
    </style>
</head>
    <body>

        <h1>Constituency Document</h1>
        <table>
            <#list summary as k, v>
                <tr>
                    <td>${k}</td><td>${v}</td>
                </tr>
            </#list>
        </table>

        <hr>

        <#list candidates as map>
            <table>

                    <#list map?keys as key>
                    <tr>
                        <#if map[key]??>
                        <td>${key}</td><td>${map[key]}</td>
                        <#else>
                        <td>${key}</td><td>-</td>
                        </#if>
                    </tr>
                    </#list>
            </table>
        <br><br><br>
        </#list>
    </body>
</html>

