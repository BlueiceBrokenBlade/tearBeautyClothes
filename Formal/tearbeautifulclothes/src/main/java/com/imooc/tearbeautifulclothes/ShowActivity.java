package com.imooc.tearbeautifulclothes;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ShowActivity extends Activity implements View.OnClickListener{
    private TearClothView tearClothView;
    private TextView tv;
    private Button btn_backmain;

    private Integer imageId1;
    private Integer imageId2;
    private Intent intent;
    private Bundle bundle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        bundle=getIntent().getExtras();
        imageId1=bundle.getInt("imgId1");
        imageId2=bundle.getInt("imgId2");


        tearClothView= (TearClothView) findViewById(R.id.tearClothView);
        tearClothView.setUpPicture(imageId1);
        tearClothView.setDownPicture(imageId2);

        tearClothView.setOnScratchCompleteListener(new TearClothView.OnScratchCompleteListener() {
            @Override
            public void complete() {
//                Toast.makeText(ShowActivity.this,"擦除完成！",Toast.LENGTH_SHORT).show();
            }
        });

        btn_backmain= (Button) findViewById(R.id.btn_backmain);
        btn_backmain.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_backmain:
                finish();
                break;
            default:
                break;
        }
    }
}
