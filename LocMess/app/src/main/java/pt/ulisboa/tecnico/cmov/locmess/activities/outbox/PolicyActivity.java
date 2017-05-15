package pt.ulisboa.tecnico.cmov.locmess.activities.outbox;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.activities.ToolbarActivity;
import pt.ulisboa.tecnico.cmov.locmess.adapters.RecyclerListsAdapter;
import pt.ulisboa.tecnico.cmov.locmess.adapters.SimpleDividerItemDecoration;
import pt.ulisboa.tecnico.cmov.locmess.activities.model.types.Policy;
import pt.ulisboa.tecnico.cmov.locmess.activities.model.types.ProfileKeypair;

public class PolicyActivity extends ToolbarActivity implements RecyclerListsAdapter.activityCallback {

    private RecyclerView recView;
    private RecyclerListsAdapter adapter;
    private ArrayList listData;
    private boolean isWhitelist = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);

        setupToolbar("LocMess - Setting up Policy");

        if (savedInstanceState != null)
            // Restore value of members from saved state
            listData = (ArrayList) savedInstanceState.getSerializable("list");
        else if (getIntent().getSerializableExtra("policy") == null)
            listData = new ArrayList();
        else {
            Policy policy = (Policy) getIntent().getSerializableExtra("policy");
            assert policy != null;
            isWhitelist = policy.isWhitelist();
            listData = (ArrayList) policy.getKeyValues();
        }

        setUpRecyclerView();

        Button addItem = (Button) findViewById(R.id.btn_add_item);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = createNewKeypairDialog();
                dialog.show();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("list", listData);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {

        Intent resultData = new Intent();
        resultData.putExtra("policy",new Policy(listData, isWhitelist));

        setResult(RESULT_OK, resultData);
        finish();
    }

    private void setUpRecyclerView() {
        //LayoutManager: GridLayout or StaggeredGridLayoutManager
        recView = (RecyclerView) findViewById(R.id.rec_list);
        recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerListsAdapter(listData, this, R.layout.listitem_profile_keypair, true);
        recView.addItemDecoration(new SimpleDividerItemDecoration(this));
        recView.setAdapter(adapter);
        adapter.setActivityCallback(this);

        setUpItemTouchHelper();
        setUpAnimationDecoratorHelper();

        setUpPolicyModeButton();
    }

    private void setUpPolicyModeButton() {
        ToggleButton toggle = (ToggleButton) findViewById(R.id.btn_policy_mode);
        toggle.setChecked(isWhitelist);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isWhitelist = isChecked ;
            }
        });
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
                        xMark = ContextCompat.getDrawable(PolicyActivity.this, R.drawable.ic_clear_24dp);
                        xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                        xMarkMargin = (int) PolicyActivity.this.getResources().getDimension(R.dimen.ic_clear_margin);
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

    private void addKeyPair(String key, String value) {
        for (ProfileKeypair keypair : (ArrayList<ProfileKeypair>) listData) {
            if (keypair.getKey().equals(key)) {
                Toast.makeText(this, "The key '" + key + "' already exists", Toast.LENGTH_LONG).show();
                return;
            }
        }
        ProfileKeypair item = new ProfileKeypair(key, value);
        listData.add(item);
        adapter.notifyItemInserted(listData.indexOf(item));
    }

    private void replaceKeyPair(int pos, String value) {
        ((ProfileKeypair) listData.get(pos)).setValue(value);
        adapter.notifyItemChanged(pos);
    }


    private void deleteItem(int pos) {
        listData.remove(pos);
        adapter.notifyItemRemoved(pos);
    }

    @Override
    public void onItemClick(int p) {
        Toast.makeText(getApplicationContext(), "TODO", Toast.LENGTH_LONG).show();
        Dialog dialog = editKeypairDialog(p);
        dialog.show();
    }


    @Override
    public void onUndoTimeout(int p) {
        deleteItem(p);
    }


    public Dialog createNewKeypairDialog() {

        final View view = setupAutoCompleteKeys();

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.new_keypair)
                .setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        addKeyPair(String.valueOf(((TextView) view.findViewById(R.id.profile_key)).getText()),
                                String.valueOf(((TextView) view.findViewById(R.id.profile_value)).getText()));
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public Dialog editKeypairDialog(final int pos) {

        final View view = setupAutoCompleteKeys();

        TextView keyTextView = (TextView) view.findViewById(R.id.profile_key);
        final TextView valueTextView = (TextView) view.findViewById(R.id.profile_value);

        ProfileKeypair keyPair = (ProfileKeypair) listData.get(pos);

        keyTextView.setText(keyPair.getKey());
        keyTextView.setFocusable(false);
        keyTextView.setClickable(false);
        keyTextView.setEnabled(false);
        keyTextView.setFocusableInTouchMode(false);

        valueTextView.setText(keyPair.getValue());


        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit keypair")
                .setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        replaceKeyPair(pos, valueTextView.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private View setupAutoCompleteKeys() {
        View view = getLayoutInflater().inflate(R.layout.dialog_new_keypair, null);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, getExistingKeys());

        final AutoCompleteTextView textView = (AutoCompleteTextView) view.findViewById(R.id.profile_key);

        textView.setAdapter(adapter);

        textView.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    textView.showDropDown();

            }
        });

        textView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                textView.showDropDown();
                return false;
            }
        });

        return view;
    }

    private List<String> getExistingKeys() {
        return application.getAvailableKeysContainer().getKeys();
    }


}
