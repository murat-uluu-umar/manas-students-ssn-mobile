package kg.manas.ssn.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kg.manas.ssn.R;
import kg.manas.ssn.service.model.Lesson;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LessonsAdapter extends RecyclerView.Adapter<LessonsAdapter.ViewHolder> implements View.OnClickListener {
    private List<Lesson> lessonList;

    public LessonsAdapter(List<Lesson> lessonList) {
        this.lessonList = lessonList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.lesson_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.time.setText(lessonList.get(position).getTime());
        holder.title.setText(lessonList.get(position).getName());
        if (lessonList.get(position).isOpen())
            actionInfo(holder.info, lessonList.get(position));
        holder.info.setTag(lessonList.get(position));
        holder.info.setText(lessonList.get(position).getInfo());
        holder.touchArea.setTag(holder.info);
        holder.cardView.setCardBackgroundColor(lessonList.get(position).getColor());
        holder.touchArea.setOnClickListener(this);
        for (Lesson lesson : lessonList) {
            Lesson les = lessonList.get(position);
            if (!les.getStartLesson().isAfter(lesson.getEndLesson())
                    && !lesson.getStartLesson().isAfter(les.getEndLesson())
                    && les != lesson) {
                holder.image.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return lessonList.size();
    }

    @Override
    public void onClick(View view) {
        TextView info = (TextView) view.getTag();
        actionInfo(info, (Lesson) info.getTag());
    }
    @SuppressLint("NonConstantResourceId")
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_lesson_title)
        TextView title;
        @BindView(R.id.item_lesson_time)
        TextView time;
        @BindView(R.id.item_lesson_info)
        TextView info;
        @BindView(R.id.item_lesson_image)
        ImageView image;
        @BindView(R.id.item_back)
        CardView cardView;
        @BindView(R.id.touchArea)
        View touchArea;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void actionInfo(TextView infoText, Lesson lesson) {
        Log.i("ACTION_INFO", lesson.isOpen() + "");
        if (!lesson.isOpen()) {
            infoText.setVisibility(View.VISIBLE);
            lesson.setOpen(true);
        } else {
            infoText.setVisibility(View.GONE);
            lesson.setOpen(false);
        }
    }
}
