import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.sql.Sql

@Grab('org.mariadb.jdbc:mariadb-java-client:2.5.2')
@GrabConfig(systemClassLoader = true)

def sql = Sql.newInstance('jdbc:mariadb://localhost:3306/arapporvideocard', 'bill', 'pass', org.mariadb.jdbc.Driver.name)

def list = []

sql.eachRow('select * from candidates') {rs ->

    def o = [:]
    o.recid = rs.id
    o.id = rs.id
    o.guid = rs.guid
    o.user_id = rs.user_id
    o.constituency_reference_id = rs.constituency_reference_id
    o.constituency = rs.constituency

    def profile = new JsonSlurper().parseText(rs.profile);
    o.en_party_independent = profile.en.party_independent
    o.en_name = profile.en.name + ". " + profile.en.initials

    if (profile.ta)
        o.ta_name = profile.ta.name
    //println JsonOutput.prettyPrint(rs.profile)
    list << o
    //println rs.profile
}

writeToJson(list)

void writeToJson(List list)
{
    def path = '/home/adithyan/all-files/coding/repo/personal-git/others/election-card-2021/card/assets/json'
    String json = JsonOutput.toJson(list)
    //new File(path, 'cand.min.json').text = json
    new File(path, 'cand.json').text = JsonOutput.prettyPrint(json)
}

void writeToTable(Sql sql, List list) {

    sql.executeUpdate("drop table if exists videocardinfo")
    String createTableQuery = "create table videocardinfo (" + list.first().keySet().collect { it + " varchar(255)" }.join(", ") + ")"

    sql.executeUpdate(createTableQuery)

    sql.withBatch(100) { stmt ->
        list.eachWithIndex { row, i ->
            String q2 = "insert into videocardinfo values(" + row.values().collect { "'" + it + "'" }.join(", ") + ")"
            println(i + 1) + '... adding to batch...' + q2
            stmt.addBatch q2
        }
    }
}


//list.eachWithIndex { row, i ->
//    println (i + 1) + '...inserting...' + q2
//    sql.executeUpdate(q2)
//}

println "done"