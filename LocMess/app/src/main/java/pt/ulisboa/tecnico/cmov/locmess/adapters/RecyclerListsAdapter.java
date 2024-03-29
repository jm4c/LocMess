package pt.ulisboa.tecnico.cmov.locmess.adapters;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.model.types.Location;
import pt.ulisboa.tecnico.cmov.locmess.model.types.Message;
import pt.ulisboa.tecnico.cmov.locmess.model.types.ProfileKeypair;

public class RecyclerListsAdapter extends RecyclerView.Adapter<RecyclerListsAdapter.AdapterViewHolder> {

    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec

    private List<Object> items;
    private LayoutInflater inflater;
    private List<Object> itemsPendingRemoval;

    private int itemLayout;

    boolean undoOn; // is undo on, you can turn it on from the toolbar menu

    public boolean isUndoOn() {
        return undoOn;
    }


    private Handler handler = new Handler(); // handler for running delayed runnables
    HashMap<Object, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be


    public RecyclerListsAdapter(List<Object> items, Context c, int itemLayout, boolean undoOn) {
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
    public AdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(itemLayout, parent, false);
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterViewHolder holder, int position) {
        Object item = items.get(position);
        Message message;

        switch (itemLayout) {
            case R.layout.listitem_inbox_message:
                message = (Message) item;
                String owner = message.getOwner();

                holder.owner.setText(owner);
                holder.title.setText(message.getTitle());
                if(message.isRead()){
                    holder.title.setTypeface(null, Typeface.NORMAL);
                    holder.owner.setTypeface(null, Typeface.NORMAL);

                }else{
                    holder.title.setTypeface(null, Typeface.BOLD);
                    holder.owner.setTypeface(null, Typeface.BOLD);
                }
                holder.subtitle.setText(message.getContent());
                holder.timestamp.setText("9:44"); //TODO timestamp when receives
                holder.thumbnail.setImageDrawable(
                        drawIconFromString(owner, holder.texDrawableBuilder));
                break;
            case R.layout.listitem_outbox_message:
                message = (Message) item;
                holder.title.setText(message.getTitle());
                holder.subtitle.setText(message.getContent());
                holder.timestamp.setText(message.getTimeWindow().printFormattedStartTime() + " to " + message.getTimeWindow().printFormattedEndTime()); //"02 April 19:00 to 03 April 18:00");

                if (((Message) item).isCentralized())
                    holder.thumbnail.setImageResource(R.drawable.ic_cloud_black_36dp);
                else
                    holder.thumbnail.setImageResource(R.drawable.ic_settings_input_antenna_black_36dp);
                break;
            case R.layout.listitem_location:
                Location location = (Location) item;
                holder.title.setText(location.getName());
                if (location.getSsidList() == null) {
                    //means its based on gps location
                    holder.subtitle.setText("[" + location.getLatitude() + ", "
                            + location.getLongitude() + ", "
                            + location.getRadius() + "m]");
                    holder.thumbnail.setImageResource(R.drawable.ic_location_on_black_36dp);
                } else {
                    //means its based on ssid
                    holder.subtitle.setText(location.getSsidList().toString());
                    holder.thumbnail.setImageResource(R.drawable.ic_wifi_black_36dp);
                }
                break;
            case R.layout.listitem_profile_keypair:
                ProfileKeypair profileKeypair = (ProfileKeypair) item;
                holder.title.setText(profileKeypair.getKey() + " = " + profileKeypair.getValue());
                break;
            case R.layout.listitem_location_ssid:
                String ssid = (String) item;
                holder.title.setText(ssid);
                break;
        }


        holder.handleUndoState(item);

    }

    private TextDrawable drawIconFromString(String value, TextDrawable.IBuilder builder) {
        // generate color based on a key (same key returns the same color)
        int color = ColorGenerator.MATERIAL.getColor(value);
        //only need first letter now
        value = String.valueOf(value.charAt(0));
        return builder.build(value, color);

    }

    public void setItems(ArrayList<Object> exerciseList) {
        this.items.clear();
        this.items.addAll(exerciseList);
    }

    public void pendingRemoval(int position) {
        final Object item = items.get(position);
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
        Object item = items.get(position);
        return itemsPendingRemoval.contains(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class AdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView owner;
        private TextView title;
        private TextView subtitle;
        private ImageView thumbnail;
        private View container;
        private TextView timestamp;
        private Button undoButton;
        private TextDrawable.IBuilder texDrawableBuilder;


        public AdapterViewHolder(View itemView) {
            super(itemView);
            //TODO set all resources from layout here
            switch (itemLayout) {
                case R.layout.listitem_inbox_message:
                    //TODO inbox resources
                    texDrawableBuilder = TextDrawable.builder()
                            .beginConfig()
                            .withBorder(4)
                            .height(100)
                            .width(100)
                            .endConfig()
                            .round();
                    owner = (TextView) itemView.findViewById(R.id.owner);
                    title = (TextView) itemView.findViewById(R.id.txt_primary);
                    subtitle = (TextView) itemView.findViewById(R.id.txt_secondary);
                    thumbnail = (ImageView) itemView.findViewById(R.id.im_item_icon);
                    timestamp = (TextView) itemView.findViewById(R.id.timestamp);
                    break;
                case R.layout.listitem_outbox_message:

                    title = (TextView) itemView.findViewById(R.id.lbl_item_title);
                    subtitle = (TextView) itemView.findViewById(R.id.lbl_item_subtitle);
                    thumbnail = (ImageView) itemView.findViewById(R.id.im_item_icon);
                    timestamp = (TextView) itemView.findViewById(R.id.timestamp);
                    break;
                case R.layout.listitem_location:
                    title = (TextView) itemView.findViewById(R.id.lbl_item_title);
                    subtitle = (TextView) itemView.findViewById(R.id.lbl_item_subtitle);
                    thumbnail = (ImageView) itemView.findViewById(R.id.im_item_icon);
                    break;
                case R.layout.listitem_profile_keypair:
                    title = (TextView) itemView.findViewById(R.id.lbl_keypair);
                    break;
                case R.layout.listitem_location_ssid:
                    title = (TextView) itemView.findViewById(R.id.lbl_ssid);
            }

            container = itemView.findViewById(R.id.cont_item_root);
            container.setOnClickListener(this);
            undoButton = (Button) itemView.findViewById(R.id.undo_button);

        }

        public void handleUndoState(final Object item) {

            // TODO handle Undo according to layout used
            if (itemsPendingRemoval.contains(item)) {
                //undo state
                itemView.setBackgroundColor(Color.RED);

                // all list items have a title
                title.setVisibility(View.GONE);

                // only location, inbox and outbox have a subtitle and a thumbnail
                if (itemLayout != R.layout.listitem_profile_keypair && itemLayout != R.layout.listitem_location_ssid) {
                    subtitle.setVisibility(View.GONE);
                    thumbnail.setVisibility(View.GONE);
                }

                //only inbox and outbox have a timestamp
                if (itemLayout == R.layout.listitem_inbox_message || itemLayout == R.layout.listitem_outbox_message) {
                    timestamp.setVisibility(View.GONE);
                }

                //only inbox has a owner
                if (itemLayout == R.layout.listitem_inbox_message) {
                    owner.setVisibility(View.GONE);
                }

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

                // all list items have a title
                title.setVisibility(View.VISIBLE);

                // only location, inbox and outbox have a subtitle and a a thumbnail
                if (itemLayout != R.layout.listitem_profile_keypair && itemLayout != R.layout.listitem_location_ssid ) {
                    subtitle.setVisibility(View.VISIBLE);
                    thumbnail.setVisibility(View.VISIBLE);
                }


                //only inbox and outbox have a timestamp
                if (itemLayout == R.layout.listitem_inbox_message || itemLayout == R.layout.listitem_outbox_message) {
                    timestamp.setVisibility(View.VISIBLE);
                }

                //only inbox has a owner
                if (itemLayout == R.layout.listitem_inbox_message) {
                    owner.setVisibility(View.VISIBLE);
                }

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
                    Object item = items.get(itemID);
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