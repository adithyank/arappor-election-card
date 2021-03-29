def file = new File('/tmp/msg.txt')


file.text.readLines().eachWithIndex { it, idx ->

    def arr = it.split("\\|")
    def ph  = arr[0]
    def msg = arr[1]

    def enmsg = URLEncoder.encode(msg, 'UTF-8')

    def url = "http://sms.indtele.com/app/smsapi/index.php?key=36048482577380&campaign=0&routeid=9&type=text&contacts=$ph&senderid=HITEKK&msg=$enmsg"

    def resp = new URL(url).text

    println "$idx : resp for $ph : $resp : msg = $msg"

    sleep 1000
    //println "exiting..."
    //System.exit(0)
}


println "done"