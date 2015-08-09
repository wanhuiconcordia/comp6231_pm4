package loggerserver;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * @author comp6231.team5
 * LoggerWriter maintain a printWriter and html related tags. It write msg to a html file.
 */
public class LoggerWriter {
	private PrintWriter loggerWriter;
		
	private String htmleader = "<html>\n<link rel=\"stylesheet\" type=\"text/css\" href=\"./settings/mystyle.css\">\n<script src=\"./settings/sorttable.js\"></script>\n<body>\n<table class='sortable' align='center'>\n<thead><tr><th width=\"20%\">timestamp</th><th width=\"20%\">host</th><th width=\"60%\">message</th></tr></thead>\n<tbody>\n";
	private String htmlTerminator = "</tbody>\n</table>\n</body>\n</html>";
	private String trHeader = "<tr>";
	private String trTerminator = "</tr>";
	private String tdHeader = "<td>";
	private String tdTerminator = "</td>";
	private boolean useHtmlStyle;
	
	private SimpleDateFormat simpleDateFormat;
	
	/**
	 * Constructor
	 * @param fileName
	 * @param useHtmlStyle
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public LoggerWriter(String fileName, boolean useHtmlStyle) throws FileNotFoundException, UnsupportedEncodingException{
		this.useHtmlStyle = useHtmlStyle;
		loggerWriter = new PrintWriter(fileName, "UTF-8");	
	
		if(useHtmlStyle){
			loggerWriter.println(htmleader);
		}
		
		simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:S");
	}
	/**
	 * write msg to file with html format in 3 coloms: timestamp, host, message
	 * @param host
	 * @param msg
	 */
	public /*synchronized*/ void write(String host, String msg){
		String timestamp = simpleDateFormat.format(Calendar.getInstance().getTime());
		if(useHtmlStyle)
		{
			loggerWriter.println(trHeader + tdHeader + timestamp + tdTerminator 
					+ tdHeader + host + tdTerminator + tdHeader +  msg + tdTerminator + trTerminator);
		}else{
			loggerWriter.println(timestamp + "\t" + host + "\t" + msg);
		}
		loggerWriter.flush();
	}
	
	/**
	 * write the html end tag
	 */
	public void stop(){
		if(useHtmlStyle){
			loggerWriter.println(htmlTerminator);
			loggerWriter.flush();
		}
	}
}
