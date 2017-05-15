package pt.ulisboa.tecnico.cmov.locmess.activities.location;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import pt.ulisboa.tecnico.cmov.locmess.activities.login.LoginActivity;
import pt.ulisboa.tecnico.cmov.locmess.activities.model.types.Location;

import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.activities.ToolbarActivity;
import pt.ulisboa.tecnico.cmov.locmess.adapters.RecyclerListsAdapter;
import pt.ulisboa.tecnico.cmov.locmess.adapters.SimpleDividerItemDecoration;


public class NewLocationSSIDActivity extends ToolbarActivity implements RecyclerListsAdapter.activityCallback {

    private RecyclerView recView;
    private RecyclerListsAdapter adapter;
    private ArrayList listData;

    private EditText nameEditText;
    private EditText ssIdEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ssid_new_location);

        setupToolbar("LocMess - New SSID Location");


        nameEditText = (EditText) findViewById(R.id.input_name);
        ssIdEditText = (EditText) findViewById(R.id.input_ssid);


        if (savedInstanceState != null)
            // Restore value of members from saved state
            listData = (ArrayList) savedInstanceState.getSerializable("list");
        else if (getIntent().getSerializableExtra("ssid") == null)
            listData = new ArrayList<>();
        else {
            listData = (ArrayList<String>) getIntent().getSerializableExtra("ssid");

        }

        setUpRecyclerView();

        Button addSSID = (Button) findViewById(R.id.addButtonSSID);
        addSSID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ssid = ssIdEditText.getText().toString();

                //Add ssid to list
                if (listData.contains(ssid)) {
                    Toast.makeText(NewLocationSSIDActivity.this, "The SSID '" + ssid + "' already exists", Toast.LENGTH_LONG).show();
                    return;
                }
                ssIdEditText.setText("");
                listData.add(ssid);
                adapter.notifyItemInserted(listData.indexOf(ssid));

            }
        });


        Button createLocation = (Button) findViewById(R.id.addButtonSSIDLocation);
        createLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Location location = new Location(nameEditText.getText().toString(), listData);
                AddLocationTask task = new AddLocationTask();
                task.execute(location);

                try {
                    Boolean result = task.get();
                    if (result == null){
                        Toast.makeText(NewLocationSSIDActivity.this, "Can't reach server, no actions done.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(result)
                        application.addLocation(location);
                    else{
                        if (application.forceLoginFlag){
                            Intent i = new Intent(NewLocationSSIDActivity.this, LoginActivity.class);
                            application.forceLoginFlag = false;
                            Toast.makeText(NewLocationSSIDActivity.this, "This session was invalid. Logging into new session.", Toast.LENGTH_LONG).show();
                            startActivity(i);
                        }
                    }

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("list", listData);
        super.onSaveInstanceState(outState);
    }


    private void setUpRecyclerView() {
        //LayoutManager: GridLayout or StaggeredGridLayoutManager
        recView = (RecyclerView) findViewById(R.id.rec_list);
        recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerListsAdapter(listData, this, R.layout.listitem_location_ssid, true);
        recView.addItemDecoration(new SimpleDividerItemDecoration(this));
        recView.setAdapter(adapter);
        adapter.setActivityCallback(this);
        setUpItemTouchHelper();
        setUpAnimationDecoratorHelper();

        // setUpPolicyModeButton();
    }


    /**
     * This is the standard support library way of implementing "swipe to delete" feature. You can do custom drawing in onChildDraw method
     * but whatever you draw will disappear once the swipe is over, and while the items are animating to their new position the recycler view
     * background will be visible. That is rarely an desired effect.
     */
    private void setUpItemTouchHelper() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createHelperCallback());
        itemTouchHelper.attachToRecyclerView(recView);
    }

    /**
     * We're gonna setup another ItemDecorator that will draw the red background in the empty space while the items are animating to thier new positions
     * after an item is removed.
     */
    private void setUpAnimationDecoratorHelper() {
        recView.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                initiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

                if (!initiated) {
                    init();
                }

                // only if animation is in progress
                if (parent.getItemAnimator().isRunning()) {

                    // some items might be animating down and some items might be animating up to close the gap left by the removed item
                    // this is not exclusive, both movement can be happening at the same time
                    // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                    // then remove one from the middle

                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    // this is fixed
                    int left = 0;
                    int right = parent.getWidth();

                    // this we need to find out
                    int top = 0;
                    int bottom = 0;

                    // find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }

        });
    }

    private ItemTouchHelper.Callback createHelperCallback() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                    // we want to cache these and not allocate anything repeatedly in the onChildDraw method
                    Drawable background;
                    Drawable xMark;
                    int xMarkMargin;
                    boolean initiated;

                    private void init() {
                        background = new ColorDrawable(Color.RED);
                        xMark = ContextCompat.getDrawable(NewLocationSSIDActivity.this, R.drawable.ic_clear_24dp);
                        xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                        xMarkMargin = (int) NewLocationSSIDActivity.this.getResources().getDimension(R.dimen.ic_clear_margin);
                        initiated = true;
                    }

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        //deleteItem(viewHolder.getAdapterPosition());
                        int swipedPosition = viewHolder.getAdapterPosition();
                        boolean undoOn = adapter.isUndoOn();
                        if (undoOn) {
                            adapter.pendingRemoval(swipedPosition);
                        } else {
                            deleteItem(swipedPosition);
                        }
                    }

                    @Override
                    public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                        int position = viewHolder.getAdapterPosition();
                        RecyclerListsAdapter adapter = (RecyclerListsAdapter) recyclerView.getAdapter();
                        if (adapter.isPendingRemoval(position)) {
                            return 0;
                        }
                        return super.getSwipeDirs(recyclerView, viewHolder);
                    }

                    @Override
                    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        View itemView = viewHolder.itemView;

                        // not sure why, but this method get's called for viewholder that are already swiped away
                        if (viewHolder.getAdapterPosition() == -1) {
                            // not interested in those
                            return;
                        }

                        if (!initiated) {
                            init();
                        }

                        // draw red background
                        background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                        background.draw(c);

                        // draw x mark
                        int itemHeight = itemView.getBottom() - itemView.getTop();
                        int intrinsicWidth = xMark.getIntrinsicWidth();
                        int intrinsicHeight = xMark.getIntrinsicWidth();

                        int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                        int xMarkRight = itemView.getRight() - xMarkMargin;
                        int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                        int xMarkBottom = xMarkTop + intrinsicHeight;
                        xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                        xMark.draw(c);
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                };
        return simpleItemTouchCallback;
    }


    private void deleteItem(int pos) {
        listData.remove(pos);
        adapter.notifyItemRemoved(pos);
    }

    @Override
    public void onItemClick(int p) {
    }

    @Override
    public void onUndoTimeout(int p) {
        deleteItem(p);
    }


}
