package com.example.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;


public class Benefit extends AppCompatActivity implements Serializable {

    Button B1,B2,B3,B4,B5,B6,B7;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benefit);
        B1 = (Button) findViewById(R.id.btn_B1);
        B2 = (Button) findViewById(R.id.btn_B2);
        B3 = (Button) findViewById(R.id.btn_B3);
        B4 = (Button) findViewById(R.id.btn_B4);
        B5 = (Button) findViewById(R.id.btn_B5);
        B6 = (Button) findViewById(R.id.btn_B6);
        B7 = (Button) findViewById(R.id.btn_B7);

        B1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                System.out.println("button clicked");
                Intent intent = new Intent(getApplicationContext(), Benefit_specific.class);
                String main_text = "연금 등 소득지원";
                String urlcategory_n = "연금등소득지원";
                intent.putExtra("info",main_text);
                intent.putExtra("info2",urlcategory_n);
                startActivity(intent);
            }
        });

        B2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                Intent intent = new Intent(getApplicationContext(), Benefit_specific.class);
                String main_text = "자립 및 취업";
                String urlcategory_n = "자립및취업";
                intent.putExtra("info",main_text);
                intent.putExtra("info2",urlcategory_n);

                startActivity(intent);
            }
        });

        B3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), Benefit_specific.class);
                String main_text = "주거지원";
                String urlcategory_n = "주거지원";
                intent.putExtra("info",main_text);
                intent.putExtra("info2",urlcategory_n);

                startActivity(intent);
            }
        });

        B4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), Benefit_specific.class);
                String main_text = "아동/여성지원";
                String urlcategory_n = "아동여성지원";
                intent.putExtra("info",main_text);
                intent.putExtra("info2",urlcategory_n);

                startActivity(intent);
            }
        });

        B5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), Benefit_specific.class);
                String main_text = "감면 및 공제";
                String urlcategory_n = "감면및공제";
                intent.putExtra("info",main_text);
                intent.putExtra("info2",urlcategory_n);

                startActivity(intent);
            }
        });

        B6.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), Benefit_specific.class);
                String main_text = "각종할인";
                String urlcategory_n = "각종할인";
                intent.putExtra("info",main_text);
                intent.putExtra("info2",urlcategory_n);

                startActivity(intent);
            }
        });

        B7.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), Benefit_specific.class);
                String main_text = "기타지원";
                String urlcategory_n = "기타지원";
                intent.putExtra("info",main_text);
                intent.putExtra("info2",urlcategory_n);

                startActivity(intent);
            }
        });
    }



}