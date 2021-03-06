package me.coley.recaf.ui.component.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * Wrapper for Tabbed Pane, providing extra abilities such as tab removal and
 * redirection.
 *
 * @author Matt
 */
@SuppressWarnings("serial")
public class TabbedPanel extends JPanel {
	/**
	 * Wrapped tabbed pane.
	 */
	private final JTabbedPane pane;
	// Caches
	private final Map<String, JComponent> titleToChild = new HashMap<>();
	private final Map<JComponent, String> childToTitle = new HashMap<>();

	public TabbedPanel() {
		setLayout(new BorderLayout());
		pane = new JTabbedPane(JTabbedPane.TOP);
		pane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		pane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				// Only close tabs when middle-clicked
				if (e.getButton() != MouseEvent.BUTTON2) {
					return;
				}
				int index = pane.getSelectedIndex();
				if (index >= 0) {
					String key = childToTitle.remove(pane.getSelectedComponent());
					titleToChild.remove(key);
					pane.remove(index);
				}
			}
		});
		add(pane, BorderLayout.CENTER);
	}

	/**
	 * Adds a tab to the panel.
	 *
	 * @param title
	 *            The tab's title.
	 * @param component
	 *            The component to fill the new tab's viewport.
	 */
	public void addTab(String title, JComponent component) {
		pane.add(title, component);
		if (!shouldCache(title)) {
			titleToChild.put(title, component);
			childToTitle.put(component, title);
		}
	}

	/**
	 * Determines if the tab with the given title and component should cached
	 * for redirection, instead of duplicating tabs.
	 *
	 * @param title
	 *            The tab's title.
	 * @return true if the tab should be cached.
	 */
	private boolean shouldCache(String title) {
		return title.contains("Error: ") || title.contains("Search ");
	}

	/**
	 * Retrieves the tab's content given the title.
	 * 
	 * @param key
	 *            Tab title.
	 * @return Component filling tab viewport.
	 */
	public JComponent getChild(String key) {
		return titleToChild.get(key);
	}

	/**
	 * @return The number of open tabs.
	 */
	public int getTabCount() {
		return pane.getTabCount();
	}

	/**
	 * @return Selected tab index.
	 */
	public int getSelectedTab() {
		return pane.getSelectedIndex();
	}

	/**
	 * @param index
	 *            Index of the tab.
	 * @return Title of tab at given index.
	 */
	public String getTitleAt(int index) {
		return pane.getTitleAt(index);
	}

	/**
	 * Set the selected tab.
	 *
	 * @param index
	 *            Index of the tab.
	 */
	public void setSelectedTab(int index) {
		pane.setSelectedIndex(index);
	}

	/**
	 * Check if a tab by the given title exists and is available for
	 * redirection.
	 *
	 * @param title
	 *            Title of the tab to check.
	 * @return true if the tab is available for redirection.
	 */
	public boolean hasCached(String title) {
		return titleToChild.containsKey(title);
	}

	/**
	 * Retrieve the index of the cached tab by its title.
	 *
	 * @param title
	 *            The title of the tab.
	 * @return The tab's index.
	 */
	public int getCachedIndex(String title) {
		for (int i = 0; i < getTabCount(); i++) {
			Component component = pane.getComponentAt(i);
			String titleFound = childToTitle.get(component);
			if (titleFound != null && titleFound.equals(title)) {
				return i;
			}
		}
		return -1;
	}
}