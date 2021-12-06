package com.example.chat.adapters;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.chat.Benefit_Info;
import com.example.chat.Benefit_specific_2;
import com.example.chat.R;

import java.util.List;

public class Benefit_Info_ListAdapter extends BaseAdapter {
    /*private ArrayList mItems = new ArrayList<>();*/
    private Context context;
    private List<Benefit_Info> b_Info;

    private String subject;

    public Benefit_Info_ListAdapter(Context context, List<Benefit_Info> BenefitList,String subject){
        this.context = context;
        this.b_Info = BenefitList;
        this.subject = subject;
        // Log.i("check","check");
    }

    @Override  //현재 관련 사업 개수
    public int getCount() {
        return b_Info.size();
    }

    @Override //특정 항목 반환
    public Benefit_Info getItem(int position) {
        return b_Info.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override  //한 사업에 대한 view 생성
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = View.inflate(context, R.layout.form_benefitinfo,null);
        TextView B_name = (TextView) v.findViewById(R.id.b_name);
        TextView B_contents = (TextView) v.findViewById(R.id.b_contents);

        //text view내용 변경
        B_name.setText(b_Info.get(position).getB_name());
        B_contents.setText(b_Info.get(position).getB_contents());


        //버튼 클릭시 해당 리스트 항목의 id 가져옴 -> 상세페이지 리스트로 넘김
        Button btn = (Button) v.findViewById(R.id.b_more);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = b_Info.get(position).getB_id();

/*              String name = b_Info.get(position).getB_name();
                String contents =b_Info.get(position).getB_contents();
                String who = b_Info.get(position).getB_who();
                String howto = b_Info.get(position).getB_howto();*/
                System.out.println("id"+id);
                Intent intent = new Intent(v.getContext(), Benefit_specific_2.class);

                intent.putExtra("front_info2", subject);
                intent.putExtra("id",id);
/*              intent.putExtra("name",name);
                intent.putExtra("contents",contents);
                intent.putExtra("whos",who);
                intent.putExtra("howto",howto);*/

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //새로운  task 생성
                v.getContext().startActivity(intent);
            }
        });












        //특정항목 반환
        v.setTag(b_Info.get(position).getB_id());

        return v;
    }

}