package org.thenesis.planetino2.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.thenesis.planetino2.backend.awt.AWTGraphics;
import org.thenesis.planetino2.backend.awt.AWTToolkit;
import org.thenesis.planetino2.bsp2D.BSPPolygon;
import org.thenesis.planetino2.bsp2D.MapLoader;
import org.thenesis.planetino2.bsp2D.RoomDef;
import org.thenesis.planetino2.game.GameObjectManager;
import org.thenesis.planetino2.game.Player;
import org.thenesis.planetino2.graphics.Screen;
import org.thenesis.planetino2.graphics.Toolkit;
import org.thenesis.planetino2.input.InputManager;
import org.thenesis.planetino2.math3D.PolygonGroup;
import org.thenesis.planetino2.math3D.Vector3D;

public class Editor implements KeyListener, MouseListener, MouseMotionListener {

	private static final long DEFAULT_ELAPSED_TIME = 1000;

	InputManager inputManager;
	EditorEngine engine;
	Map2DPanel map2dPanel;
	JPanel panel3D;

	private int panelWidth = 400;
	private int panelHeight = 300;

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Editor editor = new Editor();
				editor.createAndShowGUI();
			}
		});
	}

	private void createAndShowGUI() {

		Toolkit.setToolkit(new EditorToolkit(this));

		EditorScreen screen = (EditorScreen) Toolkit.getInstance().getScreen(panelWidth, panelHeight);
		screen.show();
		inputManager = Toolkit.getInstance().getInputManager();

		inputManager.mapToKey(EditorEngine.exit, KeyEvent.VK_ESCAPE);
		inputManager.mapToKey(EditorEngine.goForward, KeyEvent.VK_R);
		inputManager.mapToKey(EditorEngine.goForward, KeyEvent.VK_UP);
		inputManager.mapToKey(EditorEngine.goBackward, KeyEvent.VK_A);
		inputManager.mapToKey(EditorEngine.goBackward, KeyEvent.VK_DOWN);
		inputManager.mapToKey(EditorEngine.goLeft, KeyEvent.VK_Z);
		inputManager.mapToKey(EditorEngine.goLeft, KeyEvent.VK_LEFT);
		inputManager.mapToKey(EditorEngine.goRight, KeyEvent.VK_E);
		inputManager.mapToKey(EditorEngine.goRight, KeyEvent.VK_RIGHT);
		inputManager.mapToKey(EditorEngine.goUp, KeyEvent.VK_PAGE_UP);
		inputManager.mapToKey(EditorEngine.goUp, KeyEvent.VK_G);
		inputManager.mapToKey(EditorEngine.goDown, KeyEvent.VK_PAGE_DOWN);
		inputManager.mapToKey(EditorEngine.goDown, KeyEvent.VK_V);
		inputManager.mapToKey(EditorEngine.turnRight, KeyEvent.VK_F);
		inputManager.mapToKey(EditorEngine.turnLeft, KeyEvent.VK_D);
		//		inputManager.mapToMouse(EditorEngine.turnLeft, InputManager.MOUSE_MOVE_LEFT);
		//		inputManager.mapToMouse(EditorEngine.turnRight, InputManager.MOUSE_MOVE_RIGHT);
		//		inputManager.mapToMouse(EditorEngine.tiltUp, InputManager.MOUSE_MOVE_DOWN);
		//		inputManager.mapToMouse(EditorEngine.tiltDown, InputManager.MOUSE_MOVE_UP);
		//		inputManager.mapToMouse(EditorEngine.fire, InputManager.MOUSE_BUTTON_1);
		//		inputManager.mapToKey(EditorEngine.jump, KeyEvent.VK_SPACE);
		inputManager.mapToKey(EditorEngine.zoom, KeyEvent.VK_S);

		engine = new EditorEngine(screen, inputManager);
		engine.init();
		//		Thread engineThread = new Thread(engine);
		//		engineThread.start();
		//		try {
		//			Thread.sleep(5000);
		//		} catch (InterruptedException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}

		System.out.println("Created GUI on EDT? " + SwingUtilities.isEventDispatchThread());
		JFrame f = new JFrame("Planetino Editor");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLayout(new GridLayout(2, 2));
		panel3D = screen.getPanel();
		//panel3D.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
		f.add(panel3D);
		map2dPanel = new Map2DPanel(engine, panelWidth, panelHeight);
		//map2dPanel.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
		f.add(map2dPanel);
		MapInspector inspector = new MapInspector(engine, panelWidth, panelHeight);
		f.add(inspector);
		f.setResizable(false);
		f.pack();
		f.setLocationRelativeTo(null); // Center the frame
		f.setVisible(true);
		panel3D.requestFocusInWindow();

	}

	/* Event listener interfaces */

	public void keyPressed(KeyEvent e) {
		//System.out.println("[DEBUG] AWTBackend.keyPressed(): key code: " + e.getKeyCode() + " char: "+ e.getKeyChar());
		inputManager.keyPressed(e.getKeyCode());
		updateUI();
		//listener.keyPressed(e.getKeyCode(), e.getKeyChar(), e.getModifiers());
	}

	public void keyReleased(KeyEvent e) {
		//System.out.println("[DEBUG] AWTBackend.keyReleased(): key code: " + e.getKeyCode() + " char: " + e.getKeyChar());
		//listener.keyReleased(e.getKeyCode(), e.getKeyChar(), e.getModifiers());
		inputManager.keyReleased(e.getKeyCode());
		updateUI();
	}

	// Not used
	public void keyTyped(KeyEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		//System.out.println("[DEBUG] AWTBackend.mousePressed()");
		//listener.mousePressed(e.getX(), e.getY(), e.getModifiers());
		panel3D.requestFocusInWindow();
		inputManager.pointerPressed(e.getX(), e.getY());
		updateUI();
	}

	public void mouseReleased(MouseEvent e) {
		//System.out.println("[DEBUG] AWTBackend.mouseReleased()");
		//listener.mouseReleased(e.getX(), e.getY(), e.getModifiers());
		inputManager.pointerReleased();
		updateUI();
	}

	public void mouseMoved(MouseEvent e) {
		//System.out.println("[DEBUG] AWTBackend.mouseMoved()");
		//listener.mouseMoved(e.getX(), e.getY(), e.getModifiers());
		inputManager.pointerDragged(e.getX(), e.getY());
		updateUI();
	}

	public void updateUI() {
		engine.tick(DEFAULT_ELAPSED_TIME);
		map2dPanel.repaint();
	}

	//	public void windowClosing(WindowEvent e) {
	//		listener.windowClosed();
	//	}

}

class EditorScreen implements Screen {

	JPanel panel;

	private int screenWidth = 1024;
	private int screenHeight = 640;
	protected BufferedImage screenImage;
	private AWTGraphics screenGraphics;

	private InputManager inputManager;

	public EditorScreen(Editor editor, InputManager inputManager, int screenWidth, int screenHeight) {

		this.inputManager = inputManager;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;

		screenImage = new BufferedImage(screenWidth, screenWidth, BufferedImage.TYPE_INT_ARGB_PRE);
		screenGraphics = new AWTGraphics(screenImage);

		final Dimension dimension = new Dimension(screenWidth, screenHeight);
		panel = new JPanel() {

			public Dimension getMinimumSize() {
				return dimension;
			}

			public Dimension getPreferredSize() {
				return dimension;
			}

			public void paintComponent(java.awt.Graphics g) {
				super.paintComponent(g);
				g.drawImage(screenImage, 0, 0, null);
			}
		};
		panel.setFocusable(true);
		panel.addKeyListener(editor);
		panel.addMouseListener(editor);
		panel.addMouseMotionListener(editor);
	}

	public JPanel getPanel() {
		return panel;
	}

	public void show() {
		// Do nothing
	}

	public org.thenesis.planetino2.graphics.Graphics getGraphics() {
		return screenGraphics;
	}

	public void update() {
		panel.repaint();
	}

	public int getWidth() {
		return screenWidth;
	}

	public int getHeight() {
		return screenHeight;
	}

	public void restoreScreen() {
		// Do nothing
	}

}

class Map2DPanel extends JPanel {

	BufferedImage map2DImage;
	final Dimension dimension;
	private int map2DWidth = 320;
	private int map2DHeight = 320;
	private MapLoader mapLoader;
	private Graphics map2DGraphics;
	private EditorEngine engine;

	float zoomFactorX;
	float zoomFactorY;
	float shiftX;
	float shiftY;

	public Map2DPanel(EditorEngine engine, int width, int height) {
		this.engine = engine;
		this.mapLoader = engine.getLoader();
		this.map2DWidth = width;
		this.map2DHeight = height;
		setFocusable(true);
		dimension = new Dimension(map2DWidth, map2DHeight);
		map2DImage = new BufferedImage(map2DWidth, map2DHeight, BufferedImage.TYPE_INT_ARGB_PRE);
		map2DGraphics = map2DImage.getGraphics();
	}

	public void drawMap2D() {

		Vector rooms = mapLoader.getRooms();
		int roomCount = rooms.size();

		float minX = 0;
		float maxX = 0;
		float minY = 0;
		float maxY = 0;

		for (int i = 0; i < roomCount; i++) {
			RoomDef roomDef = (RoomDef) rooms.elementAt(i);
			Vector polygons = roomDef.createPolygons();
			int polygonCount = polygons.size();
			for (int j = 0; j < polygonCount; j++) {
				BSPPolygon bspPolygon = (BSPPolygon) polygons.elementAt(j);
				if (bspPolygon.isWall()) {
					//System.out.println(bspPolygon.getLine().x1 + " " + bspPolygon.getLine().y1 + " " + bspPolygon.getLine().x2 + " " + bspPolygon.getLine().y2);
					minX = Math.min(minX, bspPolygon.getLine().x1);
					minX = Math.min(minX, bspPolygon.getLine().x2);
					minY = Math.min(minY, bspPolygon.getLine().y1);
					minY = Math.min(minY, bspPolygon.getLine().y2);
					maxX = Math.max(maxX, bspPolygon.getLine().x1);
					maxX = Math.max(maxX, bspPolygon.getLine().x2);
					maxY = Math.max(maxY, bspPolygon.getLine().y1);
					maxY = Math.max(maxY, bspPolygon.getLine().y2);
				}
			}
		}

		zoomFactorX = (maxX - minX) / map2DWidth;
		zoomFactorY = (maxY - minY) / map2DHeight;
		shiftX = minX < 0 ? -minX : 0;
		shiftY = minY < 0 ? -minY : 0;

		map2DGraphics.setColor(Color.BLACK);
		map2DGraphics.fillRect(0, 0, map2DWidth, map2DHeight);
		map2DGraphics.setColor(Color.WHITE);

		for (int i = 0; i < roomCount; i++) {
			RoomDef roomDef = (RoomDef) rooms.elementAt(i);
			System.out.println("Roomdef " + roomDef.getName());
			Vector polygons = roomDef.createPolygons();
			int polygonCount = polygons.size();
			for (int j = 0; j < polygonCount; j++) {
				BSPPolygon bspPolygon = (BSPPolygon) polygons.elementAt(j);
				if (bspPolygon.isWall()) {
					System.out.println(bspPolygon.getLine().x1 + " " + bspPolygon.getLine().y1 + " " + bspPolygon.getLine().x2 + " " + bspPolygon.getLine().y2);
					int x1 = (int) ((bspPolygon.getLine().x1 + shiftX) / zoomFactorX);
					int y1 = (int) ((bspPolygon.getLine().y1 + shiftY) / zoomFactorY);
					int x2 = (int) ((bspPolygon.getLine().x2 + shiftX) / zoomFactorX);
					int y2 = (int) ((bspPolygon.getLine().y2 + shiftY) / zoomFactorY);
					map2DGraphics.drawLine(x1, y1, x2, y2);
				}
			}
		}
	}

	public void drawPlayer() {
		GameObjectManager gameObjectManager = engine.getGameObjectManager();
		Player player = (Player) gameObjectManager.getPlayer();
		int x = (int) ((player.getX() + shiftX) / zoomFactorX);
		int y = (int) ((player.getZ() + shiftY) / zoomFactorY);
		//System.out.println(x + " " + y);
		map2DGraphics.setColor(Color.BLUE);
		map2DGraphics.fillRect(x - 2, y - 2, 4, 4);

		float xf = -player.getTransform().getSinAngleY();
		float zf = -player.getTransform().getCosAngleY();
		float cosX = player.getTransform().getCosAngleX();
		float sinX = player.getTransform().getSinAngleX();
		Vector3D v = new Vector3D(cosX * xf, sinX, cosX * zf);
		v.multiply(8);
		int x2 = (int) (x + v.x);
		int y2 = (int) (y + v.z);
		map2DGraphics.setColor(Color.YELLOW);
		map2DGraphics.drawLine(x, y, x2, y2);
	}

	@Override
	public Dimension getMinimumSize() {
		return dimension;
	}

	@Override
	public Dimension getPreferredSize() {
		return dimension;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawMap2D();
		drawPlayer();

		Vector objects = mapLoader.getObjectsInMap();
		int objectCount = objects.size();
		for (int i = 0; i < objectCount; i++) {
			PolygonGroup mapObject = (PolygonGroup) objects.elementAt(i);
			int x = (int) ((mapObject.getTransform().getLocation().x + shiftX) / zoomFactorX);
			int y = (int) ((mapObject.getTransform().getLocation().z + shiftY) / zoomFactorY);
			//System.out.println(x + " " + y);
			map2DGraphics.setColor(Color.GREEN);
			map2DGraphics.fillRect(x - 2, y - 2, 4, 4);
		}

		g.drawImage(map2DImage, 0, 0, null);

		//		g.setFont(Font.getFont(Font.MONOSPACED));
		//		g.setColor(Color.WHITE);
		//		g.drawString("2D Map", 50, 50);
	}

}

class EditorToolkit extends AWTToolkit {

	private InputManager inputManager;
	private EditorScreen awtScreen;
	private Editor editor;

	EditorToolkit(Editor editor) {
		this.editor = editor;
	}

	@Override
	public Screen getScreen(int widthHint, int heightHint) {
		if (awtScreen == null) {
			awtScreen = new EditorScreen(editor, getInputManager(), widthHint, heightHint);
		}
		return awtScreen;
	}

	@Override
	public InputManager getInputManager() {
		if (inputManager == null) {
			inputManager = new EditorInputManager();
		}
		return inputManager;
	}

	class EditorInputManager extends InputManager {

		/**
		 * An invisible cursor.
		 */
		private Cursor INVISIBLE_CURSOR;

		public EditorInputManager() {
			INVISIBLE_CURSOR = java.awt.Toolkit.getDefaultToolkit().createCustomCursor(java.awt.Toolkit.getDefaultToolkit().getImage(""), new java.awt.Point(0, 0), "invisible");
		}

		public String getKeyName(int keyCode) {
			return KeyEvent.getKeyText(keyCode);
		}

		@Override
		public void setRelativeMouseMode(boolean mode) {
			isRelativeMouseModeEnabled = false;
		}

		@Override
		protected void recenterMouse() {
			// Do nothing
		}

		@Override
		public void showCursor(boolean enabled) {
			//			if (enabled) {
			//				awtScreen.panel.setCursor(Cursor.getDefaultCursor());
			//			} else {
			//				awtScreen.panel.setCursor(INVISIBLE_CURSOR);
			//			}
		}

	}

}

class MapInspector extends JPanel implements ActionListener {
	private int newNodeSuffix = 1;
	private static String ADD_COMMAND = "add";
	private static String REMOVE_COMMAND = "remove";
	private static String CLEAR_COMMAND = "clear";

	private DynamicTree treePanel;
	private EditorEngine engine;

	public MapInspector(EditorEngine engine, int panelWidth, int panelHeight) {
		super(new BorderLayout());
		this.engine = engine;

		//Create the components.
		treePanel = new DynamicTree();
		populateTree(treePanel);

		JButton addButton = new JButton("Add");
		addButton.setActionCommand(ADD_COMMAND);
		addButton.addActionListener(this);

		JButton removeButton = new JButton("Remove");
		removeButton.setActionCommand(REMOVE_COMMAND);
		removeButton.addActionListener(this);

		JButton clearButton = new JButton("Clear");
		clearButton.setActionCommand(CLEAR_COMMAND);
		clearButton.addActionListener(this);

		//Lay everything out.
		this.setPreferredSize(new Dimension(panelWidth, panelHeight));
		add(treePanel, BorderLayout.CENTER);

		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(addButton);
		panel.add(removeButton);
		panel.add(clearButton);
		add(panel, BorderLayout.LINE_END);
	}

	public void populateTree(DynamicTree treePanel) {
		//		String p1Name = new String("Parent 1");
		//		String p2Name = new String("Parent 2");
		//		String c1Name = new String("Child 1");
		//		String c2Name = new String("Child 2");
		//
		//		DefaultMutableTreeNode p1, p2;
		//
		//		p1 = treePanel.addObject(null, p1Name);
		//		p2 = treePanel.addObject(null, p2Name);
		//
		//		treePanel.addObject(p1, c1Name);
		//		treePanel.addObject(p1, c2Name);
		//
		//		treePanel.addObject(p2, c1Name);
		//		treePanel.addObject(p2, c2Name);
		MapLoader mapLoader = engine.getLoader();
		Vector rooms = mapLoader.getRooms();
		int roomCount = rooms.size();

		for (int i = 0; i < roomCount; i++) {
			RoomDef roomDef = (RoomDef) rooms.elementAt(i);
			DefaultMutableTreeNode node = treePanel.addObject(null, roomDef);
			Vector wallVertices = roomDef.getWallVertices();
			int wallVertexCount = wallVertices.size();
			for (int j = 0; j < wallVertexCount; j++) {
				RoomDef.Vertex vertex = (RoomDef.Vertex) wallVertices.elementAt(j);
				treePanel.addObject(node, vertex);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if (ADD_COMMAND.equals(command)) {
			//Add button clicked
			treePanel.addObject("New Node " + newNodeSuffix++);
		} else if (REMOVE_COMMAND.equals(command)) {
			//Remove button clicked
			treePanel.removeCurrentNode();
		} else if (CLEAR_COMMAND.equals(command)) {
			//Clear button clicked.
			treePanel.clear();
		}
	}
}

class DynamicTree extends JPanel {
	protected DefaultMutableTreeNode rootNode;
	protected DefaultTreeModel treeModel;
	protected JTree tree;
	private java.awt.Toolkit toolkit = java.awt.Toolkit.getDefaultToolkit();

	public DynamicTree() {
		super(new GridLayout(1, 0));

		rootNode = new DefaultMutableTreeNode("Map");
		treeModel = new DefaultTreeModel(rootNode);
		treeModel.addTreeModelListener(new MyTreeModelListener());

		tree = new JTree(treeModel);
		tree.setEditable(true);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setShowsRootHandles(true);

		JScrollPane scrollPane = new JScrollPane(tree);
		add(scrollPane);
	}

	/** Remove all nodes except the root node. */
	public void clear() {
		rootNode.removeAllChildren();
		treeModel.reload();
	}

	/** Remove the currently selected node. */
	public void removeCurrentNode() {
		TreePath currentSelection = tree.getSelectionPath();
		if (currentSelection != null) {
			DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (currentSelection.getLastPathComponent());
			MutableTreeNode parent = (MutableTreeNode) (currentNode.getParent());
			if (parent != null) {
				treeModel.removeNodeFromParent(currentNode);
				return;
			}
		}

		// Either there was no selection, or the root was selected.
		toolkit.beep();
	}

	/** Add child to the currently selected node. */
	public DefaultMutableTreeNode addObject(Object child) {
		DefaultMutableTreeNode parentNode = null;
		TreePath parentPath = tree.getSelectionPath();

		if (parentPath == null) {
			parentNode = rootNode;
		} else {
			parentNode = (DefaultMutableTreeNode) (parentPath.getLastPathComponent());
		}

		return addObject(parentNode, child, true);
	}

	public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child) {
		return addObject(parent, child, false);
	}

	public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child, boolean shouldBeVisible) {
		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);

		if (parent == null) {
			parent = rootNode;
		}

		treeModel.insertNodeInto(childNode, parent, parent.getChildCount());

		//Make sure the user can see the lovely new node.
		if (shouldBeVisible) {
			tree.scrollPathToVisible(new TreePath(childNode.getPath()));
		}
		return childNode;
	}

	class MyTreeModelListener implements TreeModelListener {
		public void treeNodesChanged(TreeModelEvent e) {
			DefaultMutableTreeNode node;
			node = (DefaultMutableTreeNode) (e.getTreePath().getLastPathComponent());

			/*
			 * If the event lists children, then the changed node is the child
			 * of the node we've already gotten. Otherwise, the changed node and
			 * the specified node are the same.
			 */
			try {
				int index = e.getChildIndices()[0];
				node = (DefaultMutableTreeNode) (node.getChildAt(index));
			} catch (NullPointerException exc) {
			}

			System.out.println("The user has finished editing the node.");
			System.out.println("New value: " + node.getUserObject());
		}

		public void treeNodesInserted(TreeModelEvent e) {
		}

		public void treeNodesRemoved(TreeModelEvent e) {
		}

		public void treeStructureChanged(TreeModelEvent e) {
		}
	}
}
