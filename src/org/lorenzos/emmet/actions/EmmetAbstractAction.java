
package org.lorenzos.emmet.actions;

import io.emmet.Emmet;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import org.lorenzos.utils.OutputUtils;
import org.lorenzos.emmet.editor.EmmetEditor;
import org.openide.cookies.EditorCookie;
import org.openide.text.NbDocument;

public abstract class EmmetAbstractAction implements ActionListener {

	protected List<EditorCookie> context;
	protected String action;

	public EmmetAbstractAction(List<EditorCookie> context, String action) {
		this.context = context;
		this.action = action;
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		// For each EditorCookie
		ArrayList<Integer> editorCookieDone = new ArrayList<Integer>();
		for (EditorCookie editorCookie : this.context) {
			if (editorCookieDone.contains(editorCookie.hashCode())) continue;
			editorCookieDone.add(editorCookie.hashCode()); // Store

			// Do action
			try {

				// Create JS executor and setup a Zen editor
				final Emmet emmet = Emmet.getSingleton();
				final EmmetEditor editor = EmmetEditor.create(editorCookie);

				// Create a runnable that run the Zen action
				final String zenAction = this.action;
				Runnable runZenAction = new Runnable() {
					@Override public void run() {
						emmet.runAction(editor, zenAction);
					}
				};

				// Run it in an atomic Undo/Redo
				NbDocument.runAtomic(editor.getDocument(), runZenAction);

				// Restore scrolling position
				editor.restoreInitialScrollingPosition();

			} catch (Exception ex) {
				ex.printStackTrace(OutputUtils.getErrorStream());
			}

		}
	}
	
}
