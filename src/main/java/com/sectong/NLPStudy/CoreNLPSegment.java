package com.sectong.NLPStudy;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
			System.out.println("segment time:"+ (endTime-startTime));

			// 从CoreMap中取出CoreLabel List，逐一打印出来
			List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);

			List<String> words = new ArrayList<String>();
			for (CoreLabel token : tokens) {
				String word = token.getString(TextAnnotation.class);
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

		prop.load(CoreNLPSegment.class.getResourceAsStream("/CoreNLP-chinese.properties"));
		// 载入自定义的Properties文件
		StanfordCoreNLP pipeline = new StanfordCoreNLP(prop);

		String fileName = args[0];
		String writeNameFile = "./name_seg.txt";
		String writeDescriptionFile;
		writeDescriptionFile = "./description_seg.txt";
		String characterSet = "utf8";
		String line = "";
//		File fp = new FileReader(filename);
		// 用一些文本来初始化一个注释。文本是构造函数的参数。
		System.out.println("begin .......");
		try {
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			FileWriter fw_name = new FileWriter(writeNameFile, true);
			BufferedWriter bw_name = new BufferedWriter(fw_name);
			FileWriter fw_Description = new FileWriter(writeDescriptionFile, true);
			BufferedWriter bw_Description = new BufferedWriter(fw_Description);
			line=in.readLine();

			while (line != null){
//				System.out.println("字/词");
				String[] strString =  line.split("\t");

				if(strString.length <=2){
					line = in.readLine();
					continue;
				}

//				for(int i= 0; i < strString.length; i++){
//					System.out.print(strString[i]+'\t');
//				}
//				System.out.println();
//				line = in.readLine();
				String name = strString[0] + '\t' + strString[1]+ '\t';
				String description = strString[0] + '\t' + strString[1]+ '\t';
				if(strString.length >=3) {
					List<String> words = Segment(strString[2], pipeline);
					for (String token : words) {
//						System.out.print(token + '\t');
						name += token + '\t';
					}
					name += '\n';
				}
//				if(strString.length >3){
//					List<String> words = Segment(strString[3], pipeline);
//					for (String token : words) {
////						System.out.print(token + '\t');
//						description += token + '\t';
//					}
//					description += '\n';
//				}
				String res_name = new String(name.getBytes(characterSet), characterSet);
				fw_name.write(res_name);
				String res_description = new String(description.getBytes(characterSet), characterSet);
				fw_Description.write(res_description);
				System.out.print(name);
				System.out.print(description);
				line = in.readLine();
			}
			fw_Description.close();
			fw_name.close();
		} catch (Exception e){
			System.out.println("main exception:" + e.getMessage() );
		}

//		String test = "我爱北京天安门，天安门上太阳升。";

//		Annotation annotation;
//		annotation = new Annotation("我爱北京天安门，天安门上太阳升。");
//
//		// 运行所有选定的代码在本文
//		pipeline.annotate(annotation);
//
//		// 从注释中获取CoreMap List，并取第0个值
//		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
//		CoreMap sentence = sentences.get(0);
//
//		// 从CoreMap中取出CoreLabel List，逐一打印出来
//		List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
//		System.out.println("字/词" + "\t " + "词性" + "\t " + "实体标记");
//		System.out.println("-----------------------------");
//		for (CoreLabel token : tokens) {
//			String word = token.getString(TextAnnotation.class);
////			String pos = token.getString(PartOfSpeechAnnotation.class);
////			String ner = token.getString(NamedEntityTagAnnotation.class);
////			System.out.println(word + "\t " + pos + "\t " + ner);
//			System.out.println(word);
//		}

	}

}
