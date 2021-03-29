def clFile = '/coding/repo/adithyank/arappor-election-card/assets/data/constituency-list.txt' as File
def dir = '/home/adithyan/Downloads' as File

clFile.text.eachLine {println "mkdir -p $dir/$it"}


clFile.text.eachLine {
    def s = it + '-'

    File consDir = new File(dir, it)

    String cmd = "mv $dir/$s* $consDir.absolutePath/"
    println cmd
    //println cmd.execute().errorStream.text
}

