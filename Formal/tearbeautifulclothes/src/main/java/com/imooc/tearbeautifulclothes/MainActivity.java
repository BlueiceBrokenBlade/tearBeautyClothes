package com.imooc.tearbeautifulclothes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private GridView gridView;
    private Intent intent;
    private Integer[] images={
            R.drawable.img1,R.drawable.img2,R.drawable.img3,
            R.drawable.img4,R.drawable.img5,R.drawable.img6,
            R.drawable.img7,R.drawable.img8,R.drawable.img9};
    private Integer[] images2={
            R.drawable.imged1,R.drawable.imged2,R.drawable.imged3,
            R.drawable.imged4,R.drawable.imged5,R.drawable.imged6,
            R.drawable.imged7,R.drawable.imged8,R.drawable.imged9};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView= (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(new ImageAdapter(this,images));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent=new Intent(MainActivity.this,ShowActivity.class);
                Bundle bundle=new Bundle();
                bundle.putInt("imgId1",images[position]);
                bundle.putInt("imgId2",images2[position]);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }
}
