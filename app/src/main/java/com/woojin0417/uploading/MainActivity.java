package com.woojin0417.uploading;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class MainActivity extends Activity {
    Button imgsel,upload;
    ImageView img;
    EditText idText;
    String path;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidNetworking.initialize(getApplicationContext());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        img = (ImageView)findViewById(R.id.img); // 앨범에서 불러온 사진이 담기는 뷰
        imgsel = (Button)findViewById(R.id.selimg); // 앨범으로 가는 버튼
        idText=(EditText)findViewById(R.id.idText);
        upload =(Button)findViewById(R.id.uploadimg); // 업로드 버튼
        upload.setVisibility(View.INVISIBLE);
        upload.setOnClickListener(new View.OnClickListener() { // 업로드 클릭시
            @Override
            public void onClick(View v) {
                String id = idText.getText().toString(); // 미리 로그인한 id 값 ,, 우리 프로젝트에선 원래 입력하는 곳은 필요 없음
                String area= "YS1"; // 그전 액티비티에서 불러온 지역 정보
                String photoName= "12345678"; // 랜드함수로 생성된 사진 이름 저장될 숫자 8자리
                String phoneType= "아이폰 6"; // 스피너로 선택된 핸드폰 종류
                String phoneApp="Paris"; //어플 이름
                String season="봄"; // 선택된 계절
                String time="오후"; //선택된 시간대
                String tip="구름이 너무 이쁜 날이에요"; // tip 쓰는 곳에 저장된 정보

                img.buildDrawingCache();
                Bitmap bm = img.getDrawingCache(); // 비트맵 변환
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG,50,baos);
                byte[] b = baos.toByteArray();
                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                //자랑하기에 올린글 보여줄땐 http://13.124.87.34:3000/upload 로 연결하셈
                AndroidNetworking.post("http://13.124.87.34:3000/tupload")
                        .addBodyParameter("base64", encodedImage) //전달할 인자값
                        .addBodyParameter("pic_id", id) // 전달할 인자값
                        .addBodyParameter("photoName",photoName) //전달할 인자값
                        .addBodyParameter("area",area)
                        .addBodyParameter("phoneType",phoneType)
                        .addBodyParameter("phoneApp",phoneApp)
                        .addBodyParameter("season",season)
                        .addBodyParameter("time",time)
                        .addBodyParameter("tip",tip)
                        .addHeaders("Content-Type", "multipart/form-data")
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // do anything with response
                            }
                            @Override
                            public void onError(ANError error) {
                                // handle error

                            }
                        });


            }

        });

        imgsel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fintent = new Intent(Intent.ACTION_GET_CONTENT);
                fintent.setType("image/jpeg");
                try {
                    startActivityForResult(fintent, 100);
                } catch (ActivityNotFoundException e) {

                }
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    path = getPathFromURI(data.getData());
                    img.setImageURI(data.getData());
                    Log.d("test2",data.getData().toString());
                    upload.setVisibility(View.VISIBLE);

                }
        }
    }
    private String getPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }



}