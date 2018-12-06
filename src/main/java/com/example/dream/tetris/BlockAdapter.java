package com.example.dream.tetris;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.graphics.Color;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import java.util.List;

public class BlockAdapter extends CommonAdapter
{
    Context context;
    List<Integer> mDatas;
    public BlockAdapter(Context context, List<Integer> mDatas, int mLayoutId)
    {
        super(context,mDatas,mLayoutId);
        this.context=context;
        this.mDatas=mDatas;
    }
    @Override
    public void convert(ViewHolder helper,Object item)
    {
        ImageView imageView=helper.getView(R.id.adapter_image);
        Integer integer=(Integer)item;
        if(integer>0)
        {
          //  imageView.setImageResource(StateSquare.color[integer-1]);
            //imageView.setBackgroundColor(Color.BLUE);
            Glide.with(context)
                    .load(StateSquare.color[integer-1])
                    .into(imageView);
        }
        else
        {
            imageView.setBackgroundColor(Color.parseColor("#29505B"));
        }
    }
   // @Override
    //public View getView(int position,View convertView,ViewGroup parent)
    //{
     //   return super.getView(position,convertView,parent);
        //convertView.setLayoutParams(new GridView.LayoutParams(22,22));
       // return convertView;
    //}
}
