package pt.ulisboa.tecnico.cmov.locmess.activities.location;

/**
 * Created by SONY on 10/05/2017.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import pt.ulisboa.tecnico.cmov.locmess.R;
public class PopUpActivity extends Activity {

        final Context context = this;
        private Button button;

        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_popup);

            button = (Button) findViewById(R.id.msg_received);
            // add button listener
            button.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    // set title
                    alertDialogBuilder.setTitle("Do you accept the message?");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Click yes to accept it")
                            .setCancelable(false)
                            .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // if this button is clicked, add message to inbox.


                                    /*Message msg = new Message();  //Recebo os argumentos do servidor.(ou a msg em si)
                                    AddMessageToInboxTask task = new AddMessageTask(); //TODO
                                    task.execute(msg);

                                    dialog.cancel();*/

                                }
                            })
                            .setNegativeButton("No",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // if this button is clicked, ignore message.

                                    dialog.cancel();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
            });
        }
}


