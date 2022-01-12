package it.unimi.maledettatreest;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONObject;
import it.unimi.maledettatreest.controller.CommunicationController;
import it.unimi.maledettatreest.model.LinesModel;
import it.unimi.maledettatreest.services.KeyboardManager;

public class AddPostFragment extends Fragment {

    private final static String TAG = MainActivity.TAG_BASE + "AddPostFragment";

    private CommunicationController cc;
    private LinesModel lm;
    private KeyboardManager km;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        cc = CommunicationController.getInstance(requireContext());
        lm = LinesModel.getInstance(requireContext());
        km = new KeyboardManager(requireActivity());
        return inflater.inflate(R.layout.fragment_add_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.postB).setOnClickListener(v-> {
            km.hideKeyboard();
            String delay = ((EditText)view.findViewById(R.id.delayET)).getText().toString();
            String status = ((EditText)view.findViewById(R.id.statusET)).getText().toString();
            String comment = ((EditText)view.findViewById(R.id.commentET)).getText().toString();
            String message = "";

            if(delay.isEmpty())
                delay = null;
            else if (Integer.parseInt(delay) < 0 || Integer.parseInt(delay) > 3)
                message = "Delay must be a number between 0 and 3";

            if(status.isEmpty())
                status = null;
            else if (Integer.parseInt(status) < 0 || Integer.parseInt(status) > 2)
                message = "Status must be a number between 0 and 2";

            if(comment.isEmpty())
                comment = null;
            else if(comment.length() > 100)
                message = "Comment must be shorter than 100 characters";

            if(delay == null && status == null && comment == null)
                message = "You must compile at least one field";

            if(message.isEmpty())
                cc.addPost(lm.getSelectedDir().getDid(),delay, status, comment, this::handleAddPostResponse,
                        error -> cc.handleVolleyError(error,requireContext(),TAG));
            else
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
        });
    }

    private void handleAddPostResponse(JSONObject response) {
        Toast.makeText(requireContext(), "Post successfully created", Toast.LENGTH_LONG).show();

        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigate(AddPostFragmentDirections.actionAddPostFragmentToBoardFragment());
    }
}