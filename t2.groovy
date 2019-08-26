// Some Examples of How to Parse a JSON Response Using Groovy
// set the example json response string (for a REST Request step assertion, use "def json = messageExchange.response.contentAsString")
def json = '[{"firstName":"Bob","lastName":"Smith","uniqueId":146732,"thisIsAlwaysNull":null,"jobInfo":{"title":"","type":"Peon","code":42},"reviews":[{"date":"2017-06-01","type":"Regular","rating":"Adequate"},{"date":"2017-09-15","type":"Special","rating":"Other"}]},{"firstName":"Jack","lastName":"Jones","uniqueId":746381,"thisIsAlwaysNull":null,"jobInfo":{"title":"Big Boss","type":"Management","code":1},"reviews":[{"date":"2007-11-05","type":"Initial","rating":"Spectacular"}]},{"firstName":"Will","lastName":"Tell","uniqueId":574831,"thisIsAlwaysNull":null,"jobInfo":{"title":"Sweeper","type":"Peon","code":452},"reviews":[]}]'
// parse json string using JsonSlurper - basically converts it to a groovy list
def parsedJson = new groovy.json.JsonSlurper().parseText(json)
// get data
println " Count of people returned: " + parsedJson.size()
println " Was Will's info returned (exists)? " + ( parsedJson.find { it.firstName == "Will" } != null )
println " Was Alice's info NOT returned (not exists)? " + ( parsedJson.find { it.firstName == "Alice" } == null )
println " First person's first name: " + parsedJson[0].firstName
println " Index of person with ID 746381: " + parsedJson.findIndexOf { it.uniqueId == 746381 }
println " Info for person with last name Tell: " + parsedJson.find { it.lastName == 'Tell' }
println " Jack's ID: " + ( parsedJson.find { it.firstName == 'Jack' } ).uniqueId
println " Jack Jones's job title: " + ( parsedJson.find { it.firstName == 'Jack' && it.lastName == 'Jones' } ).jobInfo.title
println " All peon job type people's first names: " + ( parsedJson.findAll { it.jobInfo.type == 'Peon' } ).firstName
println " Is Will's thisIsAlwaysNull null? " + ( ( parsedJson.find { it.firstName == 'Will' } ).thisIsAlwaysNull == null )
println " Will Tell's had this many reviews: " + ( parsedJson.find { it.firstName == 'Will' && it.lastName == 'Tell' } ).reviews.size()
println " Bob's 2017-06-01 review rating: " + ( ( parsedJson.find { it.firstName == 'Bob' } ).reviews.find { it.date == '2017-06-01' } ).rating