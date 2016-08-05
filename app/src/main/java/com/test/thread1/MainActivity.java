package com.test.thread1;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private EditText time;
    private TextView finalResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        time = (EditText) findViewById(R.id.in_time);
        button = (Button) findViewById(R.id.btn_run);
        finalResult = (TextView) findViewById(R.id.tv_result);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTaskRunner runner = new AsyncTaskRunner();
                String sleepTime = time.getText().toString();
                runner.execute(sleepTime);
            }
        });
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                int time = Integer.parseInt(params[0])*1000;
                Thread.sleep(time);
                resp = "Slept for " + params[0] + " seconds";
            } catch (InterruptedException e) {
                e.printStackTrace();
                resp = e.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }
        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            progressDialog.dismiss();
            finalResult.setText(result);
        }
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MainActivity.this, "ProgressDialog", "Wait for "+time.getText().toString()+ " seconds");
        }
        @Override
        protected void onProgressUpdate(String... text) {
            finalResult.setText(text[0]);
        }
    }
}

/*
AsyncTask and Handler are written in Java (internally they use a Thread).

The difference between Handler and AsyncTask is: Use AsyncTask when Caller thread is a UI Thread. No need to manipulate handlers, hence easier in that sense.

Handler is more transparent of the two and probably gives you more freedom/control.




Android AsyncTask is an abstract class provided by Android which gives us the liberty to perform heavy tasks in the background and keep the UI thread light thus making the application more responsive.

Android application runs on a single thread when launched. Due to this single thread model tasks that take longer time to fetch the response can make the application non-responsive. To avoid this we use android AsyncTask to perform the heavy tasks in background on a dedicated thread and passing the results back to the UI thread. Hence use of AsyncTask in android application keeps the UI thread responsive at all times.


The basic methods used in an android AsyncTask class are defined below :

doInBackground() : This method contains the code which needs to be executed in background. In this method we can send results multiple times to the UI thread by publishProgress() method. To notify that the background processing has been completed we just need to use the return statements
onPreExecute() : This method contains the code which is executed before the background processing starts
onPostExecute() : This method is called after doInBackground method completes processing. Result from doInBackground is passed to this method
onProgressUpdate() : This method receives progress updates from doInBackground method, which is published via publishProgress method, and this method can use this progress update to update the UI thread


The three generic types used in an android AsyncTask class are given below :

Params : The type of the parameters sent to the task upon execution
Progress : The type of the progress units published during the background computation
Result : The type of the result of the background computation


The AsyncTask instance must be created and invoked in the UI thread.
The methods overridden in the AsyncTask class should never be called. They’re called automatically
AsyncTask can be called only once. Executing it again will throw an exception
 */

/*
Use AsyncTask for:

Simple network operations which do not require downloading a lot of data
Disk-bound tasks that might take more than a few milliseconds

Use Java threads for:

Network operations which involve moderate to large amounts of data (either uploading or downloading)
High-CPU tasks which need to be run in the background
Any task where you want to control the CPU usage relative to the GUI thread
 */

/*
public class ThreadExampleActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_example);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            TextView myTextView = (TextView)findViewById(R.id.myTextView);
            myTextView.setText("Button Pressed");
        }
    };

	public void buttonClick(View view) {
		Runnable runnable = new Runnable() {
			public void run() {
			long endTime = System.currentTimeMillis() + 20*1000;
			while (System.currentTimeMillis() < endTime) {
					synchronized (this) {
						try {
							wait(endTime - System.currentTimeMillis());
						} catch (Exception e) {}
					}
				}
				handler.sendEmptyMessage(0);
			}
		};
		Thread mythread = new Thread(runnable);
		mythread.start();

	}
}
 */