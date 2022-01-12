package it.unimi.maledettatreest;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import org.json.JSONObject;
import it.unimi.maledettatreest.controller.CommunicationController;
import it.unimi.maledettatreest.model.LinesModel;

public class AddPostFragment extends Fragment {

    private final static String TAG = MainActivity.TAG_BASE + "AddPostFragment";

    private CommunicationController cc;
    private LinesModel lm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        cc = CommunicationController.getInstance(requireContext());
        lm = LinesModel.getInstance(requireContext());
        return inflater.inflate(R.layout.fragment_add_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.postB).setOnClickListener(v-> {
            String delay = ((EditText)view.findViewById(R.id.delayET)).getText().toString();
            String status = ((EditText)view.findViewById(R.id.statusET)).getText().toString();
            String comment = ((EditText)view.findViewById(R.id.commentET)).getText().toString();
            boolean check = true;

            if(delay.isEmpty())
                delay = null;
            else if(Integer.parseInt(delay)<0||Integer.parseInt(delay)>3) {
                check = false;
                new AlertDialog.Builder(requireContext()).setMessage("Delay must be a number between 0 and 3")
                        .setTitle(MainActivity.ERROR)
                        .setPositiveButton(MainActivity.OK, null).create().show();
            }

            if(status.isEmpty())
                status = null;
            else if(Integer.parseInt(status)<0||Integer.parseInt(status)>2) {
                check = false;
                new AlertDialog.Builder(requireContext()).setMessage("Status must be a number between 0 and 2")
                        .setTitle(MainActivity.ERROR)
                        .setPositiveButton(MainActivity.OK, null).create().show();
            }

            if(comment.isEmpty())
                comment = null;
            else if(comment.length()>100) {
                check = false;
                new AlertDialog.Builder(requireContext()).setMessage("Comment must be shorter than 100 characters")
                        .setTitle(MainActivity.ERROR)
                        .setPositiveButton(MainActivity.OK, null).create().show();
            }

            if(delay == null && status == null && comment == null){
                check = false;
                new AlertDialog.Builder(requireContext()).setMessage("You must compile at least one field")
                        .setTitle(MainActivity.ERROR)
                        .setPositiveButton(MainActivity.OK, null).create().show();
            }

            if(check)
                cc.addPost(lm.getSelectedDir().getDid(),
                        delay, status, comment, this::handleAddPostResponse,
                        error -> cc.handleVolleyError(error,requireContext(),TAG));
        });
    }

    private void handleAddPostResponse(JSONObject response) {
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigate(AddPostFragmentDirections.actionAddPostFragmentToBoardFragment());
    }
}