package kg.manas.ssn.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kg.manas.ssn.R;
import kg.manas.ssn.service.model.Mark;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MarksListAdapter extends RecyclerView.Adapter<MarksListAdapter.ViewHolder> {

    private List<Mark> marks;

    public MarksListAdapter(List<Mark> marks) {
        this.marks = marks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mark_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(marks.get(position).getName());
        holder.point.setText(marks.get(position).getPoint());
    }

    @Override
    public int getItemCount() {
        return marks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.markItemName)
        TextView name;
        @BindView(R.id.markItemPoint)
        TextView point;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
