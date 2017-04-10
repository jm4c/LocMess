package pt.ulisboa.tecnico.cmov.locmess.adapters;


import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.model.Message;

public class OutboxAdapter extends RecyclerView.Adapter<OutboxAdapter.OutboxHolder> {

    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec

    private List<Message> items;
    private LayoutInflater inflater;
    private List<Message> itemsPendingRemoval;

    private int itemLayout;

    boolean undoOn; // is undo on, you can turn it on from the toolbar menu

    public boolean isUndoOn() {
        return undoOn;
    }


    private Handler handler = new Handler(); // handler for running delayed runnables
    HashMap<Message, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be


    public OutboxAdapter(List<Message> items, Context c, int itemLayout, boolean undoOn) {
        this.inflater = LayoutInflater.from(c);
        this.items = items;
        this.itemsPendingRemoval = new ArrayList<>();
        this.itemLayout = itemLayout;
        this.undoOn = undoOn;
    }

    private activityCallback activityCallback;
    public interface activityCallback {

        void onItemClick(int p);
        void onUndoTimeout(int p);

    }

    public void setActivityCallback(final activityCallback activityCallback) {
        this.activityCallback = activityCallback;
    }

    @Override
    public OutboxHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(itemLayout, parent, false);
        return new OutboxHolder(view);
    }

    @Override
    public void onBindViewHolder(OutboxHolder holder, int position) {
        Message item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.subtitle.setText(item.getSubTitle());

        if (item.isCentralized())
            holder.thumbnail.setImageResource(R.drawable.ic_cloud_black_36dp);
        else
            holder.thumbnail.setImageResource(R.drawable.ic_settings_input_antenna_black_36dp);

        holder.handleUndoState(item);

    }

    public void setItems(ArrayList<Message> exerciseList) {
        this.items.clear();
        this.items.addAll(exerciseList);
    }

    public void pendingRemoval(int position) {
        final Message item = items.get(position);
        if (!itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.add(item);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    activityCallback.onUndoTimeout(items.indexOf(item));
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(item, pendingRemovalRunnable);
        }
    }

    public boolean isPendingRemoval(int position) {
        Message item = items.get(position);
        return itemsPendingRemoval.contains(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class OutboxHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView title;
        private TextView subtitle;
        private ImageView thumbnail;
        private View container;
        private Button undoButton;


        public OutboxHolder(View itemView) {
            super(itemView);
            //TODO
            title = (TextView) itemView.findViewById(R.id.lbl_item_location_name);
            subtitle = (TextView) itemView.findViewById(R.id.lbl_item_location_position);
            thumbnail = (ImageView) itemView.findViewById(R.id.im_item_icon);

            container = itemView.findViewById(R.id.cont_item_root);
            container.setOnClickListener(this);

            undoButton = (Button) itemView.findViewById(R.id.undo_button);

        }

        public void handleUndoState(final Message item) {
            if (itemsPendingRemoval.contains(item)) {
                //undo state
                itemView.setBackgroundColor(Color.RED);
                title.setVisibility(View.GONE);
                subtitle.setVisibility(View.GONE);
                thumbnail.setVisibility(View.GONE);


                container.setOnClickListener(null);

                undoButton.setVisibility(View.VISIBLE);
                undoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // user wants to undo the removal, let's cancel the pending task
                        Runnable pendingRemovalRunnable = pendingRunnables.get(item);
                        pendingRunnables.remove(item);
                        if (pendingRemovalRunnable != null)
                            handler.removeCallbacks(pendingRemovalRunnable);
                        itemsPendingRemoval.remove(item);
                        // this will rebind the row in "normal" state
                        notifyItemChanged(items.indexOf(item));
                    }
                });
            } else {
                //normal state
                itemView.setBackgroundColor(Color.WHITE);
                title.setVisibility(View.VISIBLE);
                subtitle.setVisibility(View.VISIBLE);
                thumbnail.setVisibility(View.VISIBLE);


                container.setOnClickListener(this);

                undoButton.setVisibility(View.GONE);
                undoButton.setOnClickListener(null);
            }
        }

        @Override
        public void onClick(View view) {
            int itemID = getAdapterPosition();
            switch (view.getId()) {
                case R.id.cont_item_root:
                    activityCallback.onItemClick(itemID);
                    break;
                case R.id.undo_button:
                    Message item = items.get(itemID);
                    // user wants to undo the removal, let's cancel the pending task
                    Runnable pendingRemovalRunnable = pendingRunnables.get(item);
                    pendingRunnables.remove(item);
                    if (pendingRemovalRunnable != null)
                        handler.removeCallbacks(pendingRemovalRunnable);
                    itemsPendingRemoval.remove(item);
                    // this will rebind the row in "normal" state
                    notifyItemChanged(items.indexOf(item));
                    break;
            }
        }

    }
}