import com.musery.NodeJSEnvironment;
import com.musery.echarts.EchartsGenerator;

public class MyTest1 {
	public static void main(String[] args){
		String option = "{\"backgroundColor\":\"transparent\",\"grid\":{\"bottom\":\"40\",\"containLabel\":true,\"left\":\"4%\",\"right\":\"40\",\"top\":\"40\"},\"legend\":{\"bottom\":10,\"data\":[],\"show\":true,\"type\":\"scroll\"},\"series\":{\"data\":[{\"label\":{\"color\":\"#000\"},\"name\":\"172.16.13.130\"},{\"label\":{\"color\":\"#000\"},\"name\":\"172.16.13.254\"},{\"label\":{\"color\":\"#000\"},\"name\":\"172.16.13.143\"},{\"label\":{\"color\":\"#000\"},\"name\":\"172.16.13.161\"},{\"label\":{\"color\":\"#000\"},\"name\":\"172.16.13.217\"},{\"label\":{\"color\":\"#000\"},\"name\":\"172.16.13.37\"},{\"label\":{\"color\":\"#000\"},\"name\":\"172.16.13.97\"}],\"emphasis\":{\"focus\":\"adjacency\"},\"layout\":\"none\",\"links\":[{\"source\":\"172.16.13.130\",\"target\":\"172.16.13.254\",\"value\":2},{\"source\":\"172.16.13.143\",\"target\":\"172.16.13.254\",\"value\":2},{\"source\":\"172.16.13.161\",\"target\":\"172.16.13.254\",\"value\":2},{\"source\":\"172.16.13.217\",\"target\":\"172.16.13.254\",\"value\":1},{\"source\":\"172.16.13.254\",\"target\":\"172.16.13.130\",\"value\":2},{\"source\":\"172.16.13.254\",\"target\":\"172.16.13.143\",\"value\":2},{\"source\":\"172.16.13.254\",\"target\":\"172.16.13.161\",\"value\":2},{\"source\":\"172.16.13.254\",\"target\":\"172.16.13.217\",\"value\":1},{\"source\":\"172.16.13.254\",\"target\":\"172.16.13.37\",\"value\":1},{\"source\":\"172.16.13.254\",\"target\":\"172.16.13.97\",\"value\":2},{\"source\":\"172.16.13.37\",\"target\":\"172.16.13.254\",\"value\":1},{\"source\":\"172.16.13.97\",\"target\":\"172.16.13.254\",\"value\":2}],\"type\":\"sankey\"},\"title\":{\"show\":false,\"text\":\"来源?的地址关系?\"},\"tooltip\":{\"trigger\":\"item\"},\"xAxis\":{\"show\":false},\"yAxis\":{\"show\":false}}";
    String option1 = "{\"xAxis\":{\"type\":\"category\",\"data\":[\"Mon\",\"Tue\",\"Wed\",\"Thu\",\"Fri\",\"Sat\",\"Sun\"]},\"yAxis\":{\"type\":\"value\"},\"series\":[{\"data\":[820,932,901,934,1290,1330,1320],\"type\":\"line\",\"smooth\":true}]}";
		EchartsGenerator.generator(
				option1, "/Users/jonathan/Downloads/x.tmp", System.out::println, System.out::println);
	}
}
