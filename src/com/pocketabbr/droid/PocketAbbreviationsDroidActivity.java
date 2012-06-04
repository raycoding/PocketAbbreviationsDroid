package com.pocketabbr.droid;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.droid.abbr.abbrdata;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

public class PocketAbbreviationsDroidActivity extends Activity {
	static abbrdata[] dataList;
	EditText searchTerm;
	ImageButton showResult;
	Spinner sortBy;
	Spinner categoryID;
	long startTime;
	boolean ExceptionFault=false;
	
	private Runnable GetResults;
	private ProgressDialog res_ProgressDialog = null; 
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contentlayout);
        
        searchTerm=(EditText)findViewById(R.id.searchTerm);
        categoryID=(Spinner)findViewById(R.id.CategoryIDChoice);
        sortBy=(Spinner)findViewById(R.id.sortByChoice);
        showResult = (ImageButton) findViewById(R.id.showResult);
        showResult.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
		
			if(isOnline())
			{
				Thread thread =  new Thread(null,GetResults,"Retrieval");
	            thread.start();
	            startTime = System.currentTimeMillis();
		        res_ProgressDialog = ProgressDialog.show(PocketAbbreviationsDroidActivity.this,    
	           "Loading...", "Retrieving Results for the Abbreaviation.",true);
		        
		        
			}
			else
			{
				Toast.makeText(PocketAbbreviationsDroidActivity.this,"Network Connectivity Failed. \nPlease make sure you have an Active Internet Connection.",Toast.LENGTH_LONG).show();	
			}
			
			
			}
		});
        
        

        GetResults = new Runnable(){
            @Override
            public void run() {
            
				try {
				
                  				  
		          interface_find(searchTerm.getText().toString(),
		        		  sortBy.getSelectedItem().toString(),
		        		  categoryID.getSelectedItem().toString());
		          
		        	
				} catch (ParserConfigurationException e) {
					
					
				} catch (SAXException e) {
					
					
				} catch (IOException e) {
				
					res_ProgressDialog.dismiss();
					ExceptionFault=true;
						
				}
				
				/*if(ExceptionFault==true)
				{
					ExceptionFault=false;
					Toast.makeText(PocketAbbreviationsDroidActivity.this,"Error in Network Connectivity... Retry",Toast.LENGTH_LONG).show();
				}*/
				
            }

        };
        
        
    }

    
    private  Runnable returnRes = new Runnable() {

		@Override
		public void run() {
			
			    res_ProgressDialog.dismiss();
	        	Intent intent = new Intent(PocketAbbreviationsDroidActivity.this, AbbreviationResults.class);
		        startActivity(intent);
		}
    };
    
    
    public boolean isOnline() {
        ConnectivityManager cm =
            (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    
    public void interface_find(String term,String sort,String category) throws ParserConfigurationException, SAXException, IOException
	{

    	
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(
				"http://www.abbreviations.com/services/v2/abbr.php");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("uid", "Put your own UID after Registration at Abbrevation API"));
			nameValuePairs
					.add(new BasicNameValuePair("tokenid", "PUT your TOKEN ID after Registration at Abbrevation API"));
			nameValuePairs.add(new BasicNameValuePair("term",term));	
		
			if(sort.equals("By Popularity"))
			{
				nameValuePairs.add(new BasicNameValuePair("sortby","p"));
			}
			else if(sort.equals("By Category"))
			{
				nameValuePairs.add(new BasicNameValuePair("sortby","c"));
			}
			else if(sort.equals("Alphabetically"))
			{
				nameValuePairs.add(new BasicNameValuePair("sortby","a"));
			}

				nameValuePairs.add(new BasicNameValuePair("searchtype","e"));
			
				if(!category.equals("All"))
			    {nameValuePairs.add(new BasicNameValuePair("categoryid",category));}
			
			try {
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				
			}
			
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			HttpResponse response =client.execute(post);
			HttpEntity r_entity = response.getEntity();
			String output=EntityUtils.toString(r_entity);

			InputSource is = new InputSource(new StringReader(output));
		    Document doc = db.parse(is);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("result");
			dataList=new abbrdata[nList.getLength()];
			for (int temp = 0; temp < nList.getLength(); temp++) {
			   Node nNode = nList.item(temp);
			   if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			      Element eElement = (Element) nNode;
			      dataList[temp]=new abbrdata();
		          dataList[temp].setCategory(getTagValue("category", eElement));
		          dataList[temp].setDefinition(getTagValue("definition", eElement));
		          
			   }
			}
			
     runOnUiThread(returnRes);

	}
	
    private static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	    Node nValue = (Node) nlList.item(0);
		return nValue.getNodeValue();
	  }
    
}