package cool.lr.novel;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.app.Activity;
import java.io.*;
import java.util.*;
import java.net.*;
import javax.xml.transform.stream.*;
import org.apache.http.conn.*;
import java.util.regex.*;
import android.provider.*;
import android.icu.text.TimeZoneFormat;
import android.icu.text.*;





public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		EditText edittext1 = (EditText) findViewById(R.id.mainEditText1);
		EditText edittext2 = (EditText) findViewById(R.id.mainEditText2);
		TextView textview1 =(TextView) findViewById(R.id.mainTextView1);
		Start s = new Start(edittext1,edittext2,textview1,this);
		s.start();
		
    }
	//查询
	public void onClick1(View view){
		Button button1 = (Button) findViewById(R.id.mainButton1);
		//ScrollView scrollview = (ScrollView) findViewById(R.id.mainScrollView);
		TextView textview1 =(TextView) findViewById(R.id.mainTextView1);
		EditText edittext1 = (EditText) findViewById(R.id.mainEditText1);
		EditText edittext2 = (EditText) findViewById(R.id.mainEditText2);
		File lr = new File(edittext2.getText().toString(),"1");
		if (lr.exists()){lr.delete();}
		//3个参数，书名，textview，当前activity
		Check check = new Check(edittext1.getText().toString(),textview1,this,edittext2);
		Thread thread = new Thread(check);
		thread.start();
		//textview1.setText("正在拼命查询...点一下就行啦");
		try{
			File f = new File(edittext2.getText().toString(),"settings");
			if (!f.exists()){f.createNewFile();}
			//if (edittext1.getText().toString().length()!=0){
			String out = edittext1.getText().toString() +"\n" +edittext2.getText().toString();
			FileWriter fw = new FileWriter(f);
			fw.write(out);
			fw.close();
		}catch(Exception e){String erro=e.toString();}
		}
		
	
	//下载
	public void onClick2(View view){
		Button button2 = (Button) findViewById(R.id.mainButton1);
		//ScrollView scrollview = (ScrollView) findViewById(R.id.mainScrollView);
		TextView textview1 =(TextView) findViewById(R.id.mainTextView1);
		EditText edittext1 = (EditText) findViewById(R.id.mainEditText1);
		EditText edittext2 = (EditText) findViewById(R.id.mainEditText2);
		Download d = new Download(edittext2.getText().toString(),textview1,this);
		d.start();
		
	}
	//结束
	public void onClick3(View view){
		Button button3 = (Button) findViewById(R.id.mainButton1);
		//ScrollView scrollview = (ScrollView) findViewById(R.id.mainScrollView);
		TextView textview1 =(TextView) findViewById(R.id.mainTextView1);
		//textview1.setText("heello eorld\n"+textview1.getText());
		EditText edittext3 = (EditText) findViewById(R.id.mainEditText2);
		File lr = new File(edittext3.getText().toString(),"1");
		try{
		lr.createNewFile();}catch(Exception e){String error = e.toString();}
		finish();
	}
}

//初始加载
class Start extends Thread{
	EditText edittext1;
	EditText edittext2;
	TextView textview;
	Activity m;
	Start(EditText edittext1,EditText edittext2,TextView textview,Activity m){
		this.edittext1=edittext1;
		this.edittext2=edittext2;
		this.textview=textview;
		this.m=m;
	}
	@Override
	public void run(){
		File lr = new File(this.edittext2.getText().toString(),"1");
		if (lr.exists()){lr.delete();}
		try{
			File f1 = new File(this.edittext2.getText().toString(),"settings");
			if (f1.exists()){
				FileReader fr = new FileReader(f1);
				BufferedReader vf = new BufferedReader(fr);
				String a;
				String aa="";
				while ((a=vf.readLine())!=null){aa+= a.trim()+"\n";}
				fr.close();
				String [] setting =aa.split("\n");
				if (setting[0].trim().length()>0){
				this.edittext1.setText(setting[0]);
				this.edittext2.setText(setting[1]);
			   
				
				Check check = new Check(this.edittext1.getText().toString(),this.textview,m,this.edittext2);
				Thread thread = new Thread(check);
				thread.start();
				}
				//this.textview.setText("正在拼命查询...点一下就行啦");

			}
		}catch(Exception e){String err=e.toString();}
	}
}


//查询类
class Check implements Runnable{
	//Feild
	String novelname;
	URL url;
	String r;
	String body = null;
	TextView textview ;
	Activity m;
	String myname;
	String show = "";
	boolean exit=false;
	EditText edittext;
	
	//构造器
	Check(String novelname,TextView textview,Activity m,EditText edittext){
		this.novelname = novelname;
		this.textview = textview;
		this.m = m;
		this.edittext=edittext;
		
	}
	
	//方法
	//get
	@Override
	public void run(){
		File lr = new File(this.edittext.getText().toString(),"1");
		while (!lr.exists()){
		m.runOnUiThread(new Runnable(){
				@Override
				public void run(){
					textview.setText("正在拼命查询...点一下就够啦");
				}
			});
		
		try{
			
			String novelname = this.novelname;
			String [] nn = novelname.split("\\.");
			url = new URL("http://zhannei.baidu.com/cse/search?s=5199337987683747968&q="+URLDecoder.decode(nn[0],"utf-8"));
			if (nn[0].equals(nn[nn.length-1]) == false){
				url = new URL("http://zhannei.baidu.com/cse/search?s=5199337987683747968&q="+URLDecoder.decode(nn[0],"utf-8")+"."+URLDecoder.decode(nn[nn.length-1],"utf-8"));
				
				}
		
		BufferedReader bf = new  BufferedReader(new InputStreamReader(url.openStream()));
		String readline;
		String readlines="";
		while ((readline = bf.readLine()) != null){
			readlines += readline + "\n";
		}
		this.body = readlines;
		
		}catch(Exception e){
			this.body = "";
			this.show = e.toString();
			
		}
		String msg = "";
		try{
		
		Pattern title = Pattern.compile("title=.*?class=.*?result-all-a\">",Pattern.DOTALL);
		Matcher str0 = title.matcher(this.body);
		while (str0.find()){
			String Title = str0.group(0).replaceAll("title=\"","").replaceAll("\" class=.*?>","").replaceAll("</span>","").trim();
		//this.show += Title + "\n";
		msg =msg + "书名: " + Title +"\n";
		break;}
			
		Pattern author = Pattern.compile("作者：</span>.*?<span>(.*?)</span>",Pattern.DOTALL);
		Matcher str = author.matcher(this.body);
		while (str.find()){
		String Author = str.group(0).replaceAll("作者.*?</span>.*?\n.*?<span>","").replaceAll("</span>","").replaceAll("<em>","").replaceAll("</em>","").trim();
		//this.show += Author + "\n";
		msg =msg + "作者: " + Author +"\n";
		break;}
		
		
		Pattern updatetime = Pattern.compile("更新时间：.*?<span class=\"result-game-item-info-tag-title\">(.*?)</span>",Pattern.DOTALL);
		Matcher str2 = updatetime.matcher(this.body);
		while (str2.find()){
			String Update = str2.group(0).replaceAll("更新时间：</span>","").replaceAll("<span class=\"result-game-item-info-tag-title\">","").replaceAll("</span>","").trim();
		//this.show += Update + "\n";
		msg =msg + "更新时间: " + Update +"\n";
		break;}
		
		Pattern lastchapter = Pattern.compile("最新章节：</span>.*?result-game-item-uspan.*?</span>",Pattern.DOTALL);
		Matcher str3 = lastchapter.matcher(this.body);
		while (str3.find()){
			String LastChapter = str3.group(0).replaceAll("最新章节：</span>","").replaceAll("<span class=.*?;\">","").replaceAll("</span>","").trim();
		//this.show += LastChapter + "\n";
		msg =msg + "最新章节: " + LastChapter +"\n";
		break;}
		
		Pattern description = Pattern.compile("<p class=\"result-game-item-desc.*?</p>",Pattern.DOTALL);
		Matcher str4 = description.matcher(this.body);
		while (str4.find()){
			String Description = str4.group(0).replaceAll("<p class.*?desc\">","").replaceAll("</p>","").replaceAll("</em>","").replaceAll("<em>","").trim();
		//this.show += LastChapter + "\n";
		msg =msg + "简介: \n" + Description +"\n";
		break;}
		
		Pattern titleurl = Pattern.compile("=\"window.location.*?class=\"game-legend-a\"",Pattern.DOTALL);
		Matcher str1 = titleurl.matcher(this.body);
		while (str1.find()){
			String Url = str1.group(0).replaceAll("=.*?'","").replaceAll("'\"","").replaceAll("class=.*?a\"","").trim();
		//this.show += Title + "\n";
		msg =msg + "章节列表: " + Url +"\n";
		this.myname = Url;
		//this.show = msg;
		break;}
		
		if ((this.show == "")){
		this.show = msg;}
		
		}catch(Exception e){
			this.show = e.toString();
		}
		
		
	    m.runOnUiThread(new Runnable(){
			@Override
			public void run(){
				textview.setText(show);
			}
		});
		String mm = this.textview.getText().toString().split("\n")[0];
		this.r=mm;
			if (!this.show.contains("作者")&&!this.show.contains("java")){
			
			
				run();
				
			/*m.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						long systime = System.currentTimeMillis();
						DateFormat dateformat = new SimpleDateFormat("hh:mm:ss",Locale.CHINA);
						String time = dateformat.format(new Date());
						String []times = time.split(":");
						String hour=String.valueOf(Integer.parseInt((times[0]))+8);
						if (hour.length() == 1){hour="0"+hour;}
						String time2 = hour + ":" +times[1] + ":" +times[2];
						textview.setText(time2+" 都没有\n"+show);
						exit=true;
					}
				});*/
			}
			break;
			
		}
		
	}
}

//下载类
class Download extends Thread{
	String url;
	URL churl;
	Activity m;
	TextView textview;
	String show="";
	String body;
	String dpath;
	String title="";
	String tttt;
	String texts;
	String again;
	
	Download(String dpath,TextView textview,Activity m){
		this.dpath = dpath;
		this.textview = textview;
		this.m = m;
	}
	@Override
	public void run(){
		File ll = new File(this.dpath,"1");
		while (!ll.exists()){
		try{
			
		url = textview.getText().toString();
		this.again=url;
		String [] urls = url.split("\n");
		this.url = urls[urls.length-1].replaceAll("章节列表: ","");
		//Pattern re = Pattern.compile("书名: .*?\n作者");
		//Matcher result = re.matcher(this.again);
		//String ttile;
		//while (result.find()){ttile=result.group(0);break;}
		if (!(this.title.trim().length()!=0))
		{
		this.title = urls[0].replaceAll("书名: ","");}
		
		churl = new URL(this.url);
		BufferedReader bf = new BufferedReader(new InputStreamReader(churl.openStream()));
		String readline;
		String readlines = "";
		while ((readline = bf.readLine()) != null){
			readlines += readline +"\n";
		}
		this.body = readlines;


		//IO Reader and Writer
		File f = new File(this.dpath,this.title+".txt");
		File f2 = new File(this.dpath,this.title+".log");
		if ((!f.getParentFile().exists())){f.getParentFile().mkdirs();}
		if (f.exists()){
			//FileReader fr = new FileReader(f);
			InputStreamReader read = new InputStreamReader(new FileInputStream(f2));
			BufferedReader br = new BufferedReader(read);
			String s;
			String ss="";
			while ((s = br.readLine()) != null){ss += s;}
			this.tttt = ss;
			//fr.close();
			//FileWriter fw = new FileWriter(f);
			}
		//文件不存在
		else{
			f.createNewFile();
			f2.createNewFile();
			//FileWriter fw = new FileWriter(f);
			this.tttt = "";
			}
		
	    ArrayList<String> chapterlist = new ArrayList<String>();
		
		Pattern chap = Pattern.compile("<dd><a href=\".*?\">.*?</a></dd>");
		Matcher str = chap.matcher(readlines);
		while (str.find()){
			String cccc = str.group(0).replace("<dd><a href=\"","").replace("</a></dd>","");
			/*if ((this.tttt.indexOf(cccc.split("\">")[1]) != 0)){
				chapterlist.add(cccc);
			}*/
			//Pattern find = Pattern.compile(cccc.split("\">")[1],Pattern.DOTALL);
			//Matcher findall = find.matcher(this.tttt);
			if (!this.tttt.contains(cccc.split("\">")[1])){
				chapterlist.add(cccc);
			}
			
			
		}
		
		FileWriter fw = new FileWriter(f,true);
		File lr = new File(this.dpath,"1");
		FileWriter f2w =  new FileWriter(f2,true);
		if (lr.exists()){lr.delete();}
		float num = chapterlist.size();
		for(int i=0;i<chapterlist.size();i++){
			if (lr.exists()){break;}
			//Thread.sleep((int)Math.random()*500+650);
			String chapter = chapterlist.get(i);
			String [] cpter = chapter.split("\">");
			BufferedReader bf1 = new BufferedReader(new InputStreamReader(new URL("http://wap.xxbiquge.com"+cpter[0].replaceAll("\"","").replaceAll("class","").replaceAll("empty","").replaceAll(" ","").replaceAll("=","")).openStream()));
			String readline1;
			String readlines1 = "";
			while ((readline1 = bf1.readLine()) != null){
				readlines1 += readline1 +"\n";
			}
			Pattern str2 = Pattern.compile("<p style=.*?<p style=",Pattern.DOTALL);
			Matcher textbody = str2.matcher(readlines1);
			while (textbody.find()){
				String text = textbody.group(0).replaceAll("<p style=.*?</a></p>","").replace("<p style=","").trim().replaceAll("<br />","\n").replaceAll("&nbsp;"," ").replaceAll("<br>","\n").replaceAll("<br/>","\n");
				String writer = cpter[1] + "\n\n" +text +"\n\n\n\n";
				fw.write(writer);
				fw.flush();
				f2w.write(cpter[1]+"\n");
				f2w.flush();
				DecimalFormat df = new DecimalFormat("0.00");
				
				String percent = df.format((i/num)*100);
				//long systime = System.currentTimeMillis();
				//DateFormat dateformat = new SimpleDateFormat("hh:mm:ss",Locale.CHINA);
				//String time = dateformat.format(new Date());
				//String []times = time.split(":");
				//String hour=String.valueOf(Integer.parseInt((times[0]))+8);
				//if (hour.length() == 1){hour="0"+hour;}
				//String time2 = hour + ":" +times[1] + ":" +times[2];
				this.show = "下载进度: "+percent +"%\n\n"+cpter[1] + "\n" + this.show.replaceAll("下载进度.*?\n\n","");
				m.runOnUiThread(new Runnable(){
						@Override
						public void run(){
							textview.setText(show);
						}
					});
			}
		
		}
		try{
		fw.close();f2w.close();this.body="下载完成\n"+this.show;}catch(IOException e){this.body = e.toString();}
		
		
		}catch(Exception e){
			this.show = "出错了，10秒后自动重试...\n"+e.toString() + "\n\n" +this.show+this.again;
			this.body = ""+this.show;
			m.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						textview.setText(show);
					}
				});
			try{
			Thread.sleep(10000);run();}catch(Exception ee){this.body = this.body+"\n"+ee.toString();}
		}
		
		m.runOnUiThread(new Runnable(){
				@Override
				public void run(){
					textview.setText(body);
				}
			});
			break;
	}
	
	
	}
	
}
