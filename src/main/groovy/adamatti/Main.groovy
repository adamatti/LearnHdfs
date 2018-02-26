package adamatti

import org.apache.camel.CamelContext
import org.apache.camel.ProducerTemplate
import org.apache.camel.impl.DefaultCamelContext
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileStatus
import org.apache.hadoop.fs.Path
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.util.Progressable

class Main {
    static void main(String [] args) {
        new Main().run()
    }

    private String hdfsRoot = "hdfs://root@localhost:9000"

    void run(){
        System.setProperty("HADOOP_USER_NAME", "root")

        Configuration configuration = new Configuration()
        FileSystem fs = FileSystem.get(new URI(hdfsRoot), configuration)

        createTmpFolder(fs)

        createFile(fs)
        writeWithCamel()

        listFolder(fs, "/tmp")
        fs.close()
    }

    private void createTmpFolder(FileSystem hdfs){
        Path folder = new Path("/tmp")

        if ( !hdfs.exists( folder )) {
            hdfs.mkdirs(folder)
        }
    }

    private void createFile(FileSystem hdfs){
        Path file = new Path("/tmp/table.html")

        if ( hdfs.exists( file )) {
            hdfs.delete( file, true )
        }

        OutputStream os = hdfs.create( file, new Progressable() {
            void progress() {
                println("...bytes written")
            }
        })

        BufferedWriter br = new BufferedWriter( new OutputStreamWriter( os, "UTF-8" ) )
        br.write("Hello World")
        br.close()
    }

    private void listFolder(FileSystem fs, String path){
        FileStatus[] fileStatus = fs.listStatus(new Path(path));
        for(FileStatus status : fileStatus){
            System.out.println(status.getPath().toString());
        }
    }

    private void writeWithCamel(){
        CamelContext context = new DefaultCamelContext()
        ProducerTemplate producerTemplate = context.createProducerTemplate()
        producerTemplate.sendBody(hdfsRoot + "/tmp/sampleFile.txt","abc")
    }
}
