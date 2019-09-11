package com.example.hp.cameracv2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SliderAdapter extends PagerAdapter {


    Context context;
    LayoutInflater inflater;

    public SliderAdapter(Context context){
        this.context = context;
    }

    public int[] slideImages = {

            R.drawable.face_detection,
            R.drawable.rgb,
            R.drawable.cardiogrambig

    };

    public String[] headings = {

            "Facial Detection",
            "RGB Extraction",
            "Heart Rate Reading"

    };

    public String[] slideDesc = {

            "Tracking the subject’s face using facial detection algorithms of OpenCV and isolation of desired region of interest from real-time video feed ",
            "Collection of RGB information of each frame and isolation of RGB signals using independent component analysis for further processing",
            "Upto 95% accurate Heart Rate readings in a matter of minutes"

    };

    @Override
    public int getCount() {
        return headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.slide_layout,container,false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.slide_image);
        TextView slideHeading = (TextView) view.findViewById(R.id.slide_heading);
        TextView slideDescription = (TextView) view.findViewById(R.id.slide_desc);

        slideImageView.setImageResource(slideImages[position]);
        slideHeading.setText(headings[position]);
        slideDescription.setText(slideDesc[position]);

        container.addView(view);

        return view;

    };

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((RelativeLayout)object);

    }
}
