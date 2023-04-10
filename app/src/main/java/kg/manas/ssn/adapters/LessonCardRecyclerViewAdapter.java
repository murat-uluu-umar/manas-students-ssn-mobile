package kg.manas.ssn.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kg.manas.ssn.R;
import kg.manas.ssn.service.model.LessonAndMarks;
import kg.manas.ssn.utils.interfaces.OnLessonItemClickListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LessonCardRecyclerViewAdapter extends RecyclerView.Adapter<LessonCardRecyclerViewAdapter.LessonCardViewHolder> implements Observer<List<LessonAndMarks>> {
    private List<LessonAndMarks> lessonsAndMarksList = new ArrayList<>();
    private OnLessonItemClickListener onLessonItemClickListener;

    public LessonCardRecyclerViewAdapter(OnLessonItemClickListener onLessonItemClickListener){
        this.onLessonItemClickListener = onLessonItemClickListener;
    }

    @NonNull
    @Override
    public LessonCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_lessons_card, parent, false);
        return new LessonCardViewHolder(layoutView, onLessonItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonCardViewHolder holder, int position) {
        holder.code.setText(lessonsAndMarksList.get(position).code);
        holder.name.setText(lessonsAndMarksList.get(position).name);
        holder.averageMark.setText(lessonsAndMarksList.get(position).averageMark);
    }

    @Override
    public int getItemCount() {
        return lessonsAndMarksList.size();
    }

    @Override
    public void onChanged(List<LessonAndMarks> marks) {
        lessonsAndMarksList = marks;
        notifyDataSetChanged();
    }

    @SuppressLint("NonConstantResourceId")
    static class LessonCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.codeofLesson)
        TextView code;
        @BindView(R.id.nameofLesson)
        TextView name;
        @BindView(R.id.averageMarkLesson)
        TextView averageMark;
        OnLessonItemClickListener onLessonItemClickListener;

        public LessonCardViewHolder(@NonNull View itemView,OnLessonItemClickListener onLessonItemClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            this.onLessonItemClickListener = onLessonItemClickListener;
        }
        @Override
        public void onClick(View view) {
            onLessonItemClickListener.onLessonItemClick(getBindingAdapterPosition());
        }
    }

}
