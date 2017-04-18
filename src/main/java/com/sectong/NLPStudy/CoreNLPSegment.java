package com.sectong.NLPStudy;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class CoreNLPSegment {

	public static  List<String> Segment(String str, StanfordCoreNLP pipeline) {
		try {
			long startTime = System.currentTimeMillis();
			Annotation annotation;
//		annotation = new Annotation("我爱北京天安门，天安门上太阳升。");
			annotation = new Annotation(str);
			// 运行所有选定的代码在本文
			pipeline.annotate(annotation);

			// 从注释中获取CoreMap List，并取第0个值
			List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
			CoreMap sentence = sentences.get(0);
			long endTime = System.currentTimeMillis();
//			System.out.println("segment time:"+ (endTime-startTime));

			// 从CoreMap中取出CoreLabel List，逐一打印出来
			List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);

			List<String> words = new ArrayList<String>();
			for (CoreLabel token : tokens) {
				String word = token.getString(TextAnnotation.class);
				if(word.equals("\""))
					continue;
				if(word.equals("\\"))
					continue;
				word.replace("'s","");
//				word.replace("\"","");
//				word.replace("'","");
				words.add(word);
//			String pos = token.getString(PartOfSpeechAnnotation.class);
//			String ner = token.getString(NamedEntityTagAnnotation.class);
//			System.out.println(word + "\t " + pos + "\t " + ner);
//			System.out.println(word);
			}
			return  words;
		} catch (Exception e) {
			System.out.println("segment exception:" + e.getMessage());
			return null;
		}

	}
	/**
	 * 读取filePath的文件，将文件中的数据按照行读取到String数组中
	 * @param filePath    文件的路径
	 * @return            文件中一行一行的数据
	 */
	public static String[] readToString(String filePath)
	{
		File file = new File(filePath);
		Long filelength = file.length(); // 获取文件长度
		byte[] filecontent = new byte[filelength.intValue()];
		try
		{
			FileInputStream in = new FileInputStream(file);
			in.read(filecontent);
			in.close();
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		String[] fileContentArr = new String(filecontent).split("\r\n");

		return fileContentArr;// 返回文件内容,默认编码
	}
	public static void main(String[] args) throws IOException {
		//读取配置文件
		if(args.length < 1 ) {
			System.out.println("pls input segment file:");
			System.exit(0);
		}
		Properties prop = new Properties();
		DbUility db = new DbUility();
		if(!db.InitMysqlConnection()){
			System.out.println("db connection faild");
			System.exit(0);
		}
		prop.load(CoreNLPSegment.class.getResourceAsStream("/CoreNLP-chinese.properties"));
		// 载入自定义的Properties文件
		StanfordCoreNLP pipeline = new StanfordCoreNLP(prop);

		String fileName = args[0];
		String line = "";
//		File fp = new FileReader(filename);
		// 用一些文本来初始化一个注释。文本是构造函数的参数。
		System.out.println("begin .......");
		try {
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			line=in.readLine();
			ObjectMapper mapper = new ObjectMapper();

			while (line != null){
//				System.out.println("字/词");
				String[] strString =  line.split("\t");

				if(strString.length <=2){
					line = in.readLine();
					continue;
				}

				String sql = "select id from short_video_segment where pid = '" + strString[0] + "'";
//				System.out.println("sql:"+ sql);
				if(db.Select(sql)){
					System.out.println("pid "+ strString[0] + "is find");
					line = in.readLine();
					continue;
				}
				String jsonWords_name = mapper.writeValueAsString(new ArrayList<String >());
				String jsonWords_description = mapper.writeValueAsString(new ArrayList<String >());
//				String name = strString[0] + '\t' + strString[1]+ '\t';
//				String description = strString[0] + '\t' + strString[1]+ '\t';
				System.out.println(strString[2]);
				if(strString.length >=3) {
					String name = strString[2].replaceAll("\"","");
					name = name.replaceAll("'s","");
					List<String> words = Segment(name, pipeline);
					jsonWords_name = mapper.writeValueAsString(words);
				}
				System.out.println(strString[3]);
				if(strString.length >3){
					String des = strString[3].replaceAll("\"","");
					des = des.replaceAll("'s", "");
					List<String> words = Segment(des, pipeline);
					jsonWords_description = mapper.writeValueAsString(words);
				}
				String updateSql = "insert into short_video_segment (pid, duration, name_seg, description_seg) " +
						"values ('" + strString[0] + "'," + Integer.valueOf(strString[1]) + ",'" + jsonWords_name +
						"','" + jsonWords_description + "')";
				System.out.println("sql:"+updateSql);
				System.out.println(jsonWords_name + "\t" + jsonWords_description);
				if(db.Insert(updateSql) <=0){
					System.out.println("pid:"+strString[0]+" insert sql error");
					line = in.readLine();
					continue;
				}
//				System.out.print(name);
//				System.out.print(description);
				line = in.readLine();
			}
		} catch (Exception e){
			System.out.println("main exception:" + e.getMessage() );
		}

	}

}
