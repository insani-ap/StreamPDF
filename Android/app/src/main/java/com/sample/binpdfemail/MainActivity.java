package com.sample.binpdfemail;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.sample.binpdfemail.util.MailSender;
import com.sample.binpdfemail.util.RetrofitConf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private final String url = ""; //Your Server URL
    private final String host = ""; //Mail Host URL
    private final String emailSender = "";
    private final String emailSenderPassword = "";
    private final String emailRecipient = "";
    private final String emailSubject = "";
    private final String emailBody = "";

    private final String fileName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hitAPI(host);
    }

    private void hitAPI(String url) {
        try {
            RetrofitConf.PdfApiService service = RetrofitConf.initConf(url).create(RetrofitConf.PdfApiService.class);
            Call<ResponseBody> request = service.doGetPdf();
            request.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    Thread thread = new Thread(() -> {
                        savePDF(response.body());
                        sendEmail(emailSender, emailSenderPassword, emailRecipient);
                    });
                    thread.start();
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Log.e(TAG, t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void savePDF(ResponseBody body) {
        try {
            InputStream inputStream = null;
            OutputStream outputStream = null;

            File newPDF = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + File.separator + "PDF");
            if (!newPDF.exists()){
                newPDF.mkdirs();
            }

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + File.separator + "PDF" + File.separator + fileName + ".pdf"));

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.d(TAG, "File download: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void sendEmail(String sender, String senderPassword, String recipient) {
        try {
            MailSender mail = new MailSender(sender, senderPassword, host);
            mail.sendMail(emailSubject, emailBody, sender, recipient, fileName);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}