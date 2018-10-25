package com.ufu.crokage.config;

public class CrokageStaticData {
	/*
	 * Must be set
	 */
	public static String BIKER_HOME = "/home/rodrigo/projects/BIKER/StackOverflow";                 //biker installation dir
	public static String CROKAGE_HOME = "/home/rodrigo/Dropbox/Doutorado/projects/bot";             //root of this project
	public static String BIKER_RUNNER_PATH = BIKER_HOME+"/main/biker_runner_external_queries.py";   //the program which is called by the script. Must only exist.
	public static String TMP_DIR = "/home/rodrigo/tmp";										        //dir used to generate some files
	public static String FAST_TEXT_INSTALLATION_DIR = "/home/rodrigo/projects/fastText-0.1.0";	    //fastText installation dir 
	public static String FAST_TEXT_MODEL_PATH = TMP_DIR+"/fastTextModel.bin";                       //generated model path
	
	/*
	 * Must to be provided or generated via method call
	 */
	public static String BIG_MAP_INVERTED_INDEX_APIS_FILE_PATH = TMP_DIR+"/bigMap.txt";                // generated by method call - a map containing all apis and their answers ids
	public static String REDUCED_MAP_INVERTED_INDEX_APIS_FILE_PATH = TMP_DIR+"/reducedMapReport.csv";  // generated by method call - a map containing apis with a minimum count number
	public static String DISCONSIDERED_POSTS_FILE_PATH = TMP_DIR+"/disconsideredPostsBigMap.txt";      // generated by method call by the same method above - answer ids disconsidered because have no api calls
	public static String SO_ANSWERS_IDS_PARENT_IDS_MAP = TMP_DIR+"/soAnswersIdsParentsIds.txt";          // generated by method call. 
	public static String SO_QUESTIONS_IDS_TITLES_MAP = TMP_DIR+"/soQuestionsIdsTitles.txt";            // generated by method call. Contains ids and titles from SO questions.
	public static String SO_CONTENT_FILE = TMP_DIR+"/soContent.txt";                                    // generated by method call. Contains titles, bodies and code of SO posts, one per line
	public static String SO_IDF_VOCABULARY = TMP_DIR+"/soIDFVocabulary.txt";                            // generated by method call. From so content file, calculate the idf of all words and save in this file.
	public static String SO_SET_OF_WORDS = TMP_DIR+"/soSetOfWords.txt";                                 // generated by method call. From the idf file, build a file containing the non-repeated words of SO. Used to build the vectors for all words.
	public static String SO_CONTENT_WORD_VECTORS = TMP_DIR+"/soContentWordVec.txt";                     // generated in shell through fastText commands. Uses the file above. Contains word vectors for each word of the titles vocabulary.
	public static String SO_DIRECTORY_FILES =  TMP_DIR+"/sodirectory";                                  // generated by method call. To be used by lucene to build idf voc.
	public static String SO_DIRECTORY_INDEX =  TMP_DIR+"/sodirindex";                                  // generated by method call. To be used by lucene to build idf voc.
	public static String GOOGLE_TOP_RESULTS_FOR_NLP2API = CROKAGE_HOME+"/data/googleNLP2ApiResults-all-questions.txt";  // generated by method call. Top k results for google search.
	public static String GOOGLE_EXCEPTIONS_FOR_NLP2API = CROKAGE_HOME+"/data/googleExceptionsForNLP2ApiQuestions.txt";  // questions not relevant enought for evaluation
	public static String NLP2API_QUERIES_AND_SO_QUESTIONS_TO_EVALUATE = CROKAGE_HOME+"/data/NLP2APISOQuestionsToEvaluate.csv";  // to evaluate questions to queries 
	
	/*
	 * Must to be generated or provided for tests
	 */
	public static String NLP2API_GOLD_SET_FILE = CROKAGE_HOME+"/data/nlp2ApiGoldSet.txt";            		 //for tests - static file - must be provided 
	public static String INPUT_QUERIES_FILE_CROKAGE = CROKAGE_HOME+"/data/inputQueriesCrokage.txt";          //for tests - provided or generated by reading excel file containing evaluations
	public static String INPUT_QUERIES_FILE_NLP2API = CROKAGE_HOME+"/data/inputQueriesNlp2Api.txt";          //for tests -provided or generated by reading excel file containing evaluations
	
	
	/*
	 * automatically generated files
	 */
	public static String BIKER_INPUT_QUERIES_FILE = BIKER_HOME+ "/data/inputQueries";                 //generated to be read by BIKER 
	public static String BIKER_OUTPUT_QUERIES_FILE = BIKER_HOME+ "/data/queriesApisOutput.txt";       //generated by BIKER
	public static String BIKER_SCRIPT_FILE = "/tmp/bikerCaller.sh";    							      //generated to call biker approach
	public static String RACK_INPUT_QUERIES_FILE = CROKAGE_HOME+"/data/rackApiQueriesInput.txt";      //generated to feed RACK jar
	public static String RACK_OUTPUT_QUERIES_FILE = CROKAGE_HOME+"/data/rackApiQueriesOutput.txt";    //generated after calling NLP2Api jar
	public static String NLP2API_INPUT_QUERIES_FILE = CROKAGE_HOME+"/data/nlp2apiQueriesInput.txt";   //generated to feed NLP2Api jar
	public static String NLP2API_OUTPUT_QUERIES_FILE = CROKAGE_HOME+"/data/nlp2apiQueriesOutput.txt"; //generated after calling NLP2Api jar
	
	
	/*
	 * Comes with project
	 */
	public static String STOP_WORDS_FILE_PATH = CROKAGE_HOME+"/data/stanford_stop_words.txt";         //from stanford: https://github.com/stanfordnlp/CoreNLP/blob/master/data/edu/stanford/nlp/patterns/surface/stopwords.txt
}
