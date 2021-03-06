package org.thenesis.planetino2.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;

import org.thenesis.planetino2.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.thenesis.planetino2.backend.awt.AWTGraphics;
import org.thenesis.planetino2.backend.awt.AWTToolkit;
import org.thenesis.planetino2.backend.awt.ResourceLoaderSE;
import org.thenesis.planetino2.bsp2D.BSPPolygon;
import org.thenesis.planetino2.bsp2D.RoomDef;
import org.thenesis.planetino2.bsp2D.RoomDef.Ceil;
import org.thenesis.planetino2.bsp2D.RoomDef.Floor;
import org.thenesis.planetino2.bsp2D.RoomDef.HorizontalAreaDef;
import org.thenesis.planetino2.game.GameObjectManager;
import org.thenesis.planetino2.game.Player;
import org.thenesis.planetino2.graphics.Screen;
import org.thenesis.planetino2.graphics.Toolkit;
import org.thenesis.planetino2.input.InputManager;
import org.thenesis.planetino2.loader.MapLoader;
import org.thenesis.planetino2.loader.ObjectLoader;
import org.thenesis.planetino2.loader.ResourceLoader;
import org.thenesis.planetino2.loader.ObjectLoader.Material;
import org.thenesis.planetino2.math3D.PointLight3D;
import org.thenesis.planetino2.math3D.PolygonGroup;
import org.thenesis.planetino2.math3D.Transform3D;
import org.thenesis.planetino2.math3D.Vector3D;

public class Editor implements KeyListener, MouseListener, MouseMotionListener {

	private static final long DEFAULT_ELAPSED_TIME = 1000;

	InputManager inputManager;
	EditorEngine engine;
	JFrame editorFrame;
	Map2DPanel map2dPanel;
	JPanel panel3D;

	private int panelWidth = 400;
	private int panelHeight = 300;

	private MapInspector mapInspector;
	private ObjectInspector objectInspector;
	private ResourceBrowser resourceBrowser;
	private Object selectedMapObject;
	
	private JFileChooser fileChooser = new JFileChooser();

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Editor editor = new Editor();
				editor.createAndShowGUI();
			}
		});
	}

	private void createAndShowGUI() {
		
		/* Engine */

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
		inputManager.mapToMouse(EditorEngine.turnLeft, InputManager.MOUSE_MOVE_LEFT);
		inputManager.mapToMouse(EditorEngine.turnRight, InputManager.MOUSE_MOVE_RIGHT);
		inputManager.mapToMouse(EditorEngine.tiltUp, InputManager.MOUSE_MOVE_DOWN);
		inputManager.mapToMouse(EditorEngine.tiltDown, InputManager.MOUSE_MOVE_UP);
		inputManager.mapToMouse(EditorEngine.fire, InputManager.MOUSE_BUTTON_1);
		inputManager.mapToKey(EditorEngine.jump, KeyEvent.VK_SPACE);
		inputManager.mapToKey(EditorEngine.zoom, KeyEvent.VK_S);

		engine = new EditorEngine(screen, inputManager, Toolkit.getInstance().getResourceLoader());
		engine.loadMap("TownOfFury.map"); //quake-one_bot.map
		engine.init();
		engine.tick(DEFAULT_ELAPSED_TIME);
		//		Thread engineThread = new Thread(engine);
		//		engineThread.start();
		//		try {
		//			Thread.sleep(5000);
		//		} catch (InterruptedException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		
		/* Frame */

		log("Created GUI on EDT? " + SwingUtilities.isEventDispatchThread());
		editorFrame = new JFrame("Planetino Map Editor");
		editorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		editorFrame.setLayout(new GridLayout(2, 3));
		
		/* File chooser */
		
		fileChooser = new JFileChooser("trunk/demos/resources/res");
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				String extension = getExtension(f);
				if ((extension != null) && extension.equals("map")) {
					return true;
				}
				return false;
			}

			@Override
			public String getDescription() {
				return ".map";
			}
			
		    public String getExtension(File f) {
		        String ext = null;
		        String s = f.getName();
		        int i = s.lastIndexOf('.');

		        if (i > 0 &&  i < s.length() - 1) {
		            ext = s.substring(i+1).toLowerCase();
		        }
		        return ext;
		    }
			
		});
		
		/* Menu */
		
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);
		JMenuItem menuItem = new JMenuItem("Load", KeyEvent.VK_L);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				int returnVal = fileChooser.showOpenDialog(editorFrame);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fileChooser.getSelectedFile();
		            //String filename = "trunk/demos/resources/res/test.map";
		            String filename = file.getName();
		            engine.loadMap(filename);
		    		engine.init();
		    		engine.tick(DEFAULT_ELAPSED_TIME);
		    		mapInspector.populateTree();
		    		notifyMapChanged();
		        }
		       
			}
		});
		menu.add(menuItem);
		menuItem = new JMenuItem("Save", KeyEvent.VK_S);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String filename = "trunk/demos/resources/res/test.map";
				engine.saveMap(filename);
			}
		});
		menu.add(menuItem);
		menuItem = new JMenuItem("Save As");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				int returnVal = fileChooser.showSaveDialog(editorFrame);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fileChooser.getSelectedFile();
					try {
						 //String filename = "trunk/demos/resources/res/test.map";
						String filename = file.getCanonicalPath();
						engine.saveMap(filename);
					} catch (IOException e) {
						e.printStackTrace();
					}
		        }
			}
		});
		menu.add(menuItem);
		editorFrame.setJMenuBar(menuBar);
		
		/* Panels */
		
		panel3D = screen.getPanel();
		//panel3D.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
		editorFrame.add(panel3D);
		map2dPanel = new Map2DPanel(this, panelWidth, panelHeight);
		//map2dPanel.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
		editorFrame.add(map2dPanel);
		ToolsPanel toolsPanel = new ToolsPanel(this);
		editorFrame.add(toolsPanel);
		mapInspector = new MapInspector(this, panelWidth, panelHeight);
		editorFrame.add(mapInspector);
		objectInspector = new ObjectInspector(this, panelWidth, panelHeight);
		editorFrame.add(objectInspector);
		editorFrame.setResizable(false);
		resourceBrowser = new ResourceBrowser(this);
		editorFrame.add(resourceBrowser);
		editorFrame.pack();
		editorFrame.setLocationRelativeTo(null); // Center the frame
		editorFrame.setVisible(true);
		panel3D.requestFocusInWindow();
		
		//updateUI();

	}
	
	public EditorEngine getEngine() {
		return engine;
	}
	
	public void setSelectedMapObject(Object mapObject) {
		this.selectedMapObject = mapObject;
		objectInspector.setMapObject(mapObject);
		updateUI();
		log("Selected map object: " + mapObject);
	}
	
	public Object getSelectedMapObject() {
		return selectedMapObject;
	}
	
	public static void log(String s) {
		System.out.println(s);
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
		//System.out.println("[DEBUG] AWTBackend.mouseDragged()");
		inputManager.pointerDragged(e.getX(), e.getY());
		updateUI();
	}

	public void mousePressed(MouseEvent e) {
		//System.out.println("[DEBUG] AWTBackend.mousePressed()");
		//listener.mousePressed(e.getX(), e.getY(), e.getModifiers());
		panel3D.requestFocusInWindow();
		inputManager.pointerPressed(e.getX(), e.getY(), convertMouseButtonCode(e));
		updateUI();
	}

	public void mouseReleased(MouseEvent e) {
		//System.out.println("[DEBUG] AWTBackend.mouseReleased()");
		//listener.mouseReleased(e.getX(), e.getY(), e.getModifiers());
		inputManager.pointerReleased(convertMouseButtonCode(e));
		updateUI();
	}

	public void mouseMoved(MouseEvent e) {
		//System.out.println("[DEBUG] AWTBackend.mouseMoved()");
		//listener.mouseMoved(e.getX(), e.getY(), e.getModifiers());
		
		//inputManager.pointerDragged(e.getX(), e.getY());
		//updateUI();
	}
	
	private int convertMouseButtonCode(MouseEvent e) {
		int awtButton = e.getButton();
		switch(awtButton) {
		case MouseEvent.BUTTON1:
			return InputManager.MOUSE_BUTTON_1;
		case MouseEvent.BUTTON2:
			return InputManager.MOUSE_BUTTON_2;
		case MouseEvent.BUTTON3:
			return InputManager.MOUSE_BUTTON_3;
		default:
			throw new IllegalStateException();
		}
	}

	public void updateUI() {
		engine.tick(DEFAULT_ELAPSED_TIME);
		editorFrame.pack();
		map2dPanel.repaint();
		mapInspector.repaint();
		objectInspector.repaint();
	}
	
	public void notifyMapChanged() {
		Transform3D savedPlayerTransform = (Transform3D)engine.getGameObjectManager().getPlayer().getTransform().clone();
		engine.rebuildMap();
		engine.init();
		engine.getGameObjectManager().getPlayer().getTransform().setTo(savedPlayerTransform);
		updateUI();
	}

	public void applyMaterial(Material material) {
		if (selectedMapObject instanceof RoomDef) {
			RoomDef roomDef = (RoomDef)getSelectedMapObject();
			roomDef.setFloorMaterial(material);
			roomDef.setCeilMaterial(material);
			Vector vertices = roomDef.getWallVertices();
			int size = vertices.size();
			for (int i = 0; i < size; i++) {
				RoomDef.Vertex vertex = (RoomDef.Vertex)vertices.elementAt(i);
				roomDef.setVertexMaterial(vertex, material);
			}
			notifyMapChanged();
		} else if (selectedMapObject instanceof RoomDef.Vertex) {
			RoomDef.Vertex vertex = (RoomDef.Vertex)getSelectedMapObject();
			RoomDef roomDef = vertex.getRoomDef();
			roomDef.setVertexMaterial(vertex, material);
			notifyMapChanged();
		} else if (selectedMapObject instanceof RoomDef.Ceil) {
			RoomDef.Ceil ceil = (RoomDef.Ceil)getSelectedMapObject();
			RoomDef roomDef = ceil.getRoomDef();
			roomDef.setCeilMaterial(material); // Warning: don't reuse ceil object after this !
			notifyMapChanged();
		} else if (selectedMapObject instanceof RoomDef.Floor) {
			RoomDef.Floor floor = (RoomDef.Floor)getSelectedMapObject();
			RoomDef roomDef = floor.getRoomDef();
			roomDef.setFloorMaterial(material); // Warning: don't reuse floor object after this !
			notifyMapChanged();
		}
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
	
	public void setFullScreen(boolean fullscreen) {
		// Do nothing
	}

}

class Map2DPanel extends JPanel implements MouseListener {

	BufferedImage map2DImage;
	final Dimension dimension;
	private int map2DWidth = 320;
	private int map2DHeight = 320;
	private Graphics map2DGraphics;
	private Editor editor;

	float zoomFactorX;
	float zoomFactorY;
	float shiftX;
	float shiftY;

	public Map2DPanel(Editor editor, int width, int height) {
		this.editor = editor;
		this.map2DWidth = width;
		this.map2DHeight = height;
		setFocusable(true);
		dimension = new Dimension(map2DWidth, map2DHeight);
		map2DImage = new BufferedImage(map2DWidth, map2DHeight, BufferedImage.TYPE_INT_ARGB_PRE);
		map2DGraphics = map2DImage.getGraphics();
		addMouseListener(this);
		addMouseMotionListener(editor);
	}

	public void drawMap2D() {

		EditorEngine engine = editor.getEngine();
		MapLoader mapLoader = engine.getLoader();
		
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
			if (editor.getSelectedMapObject() == roomDef) {
				map2DGraphics.setColor(Color.RED);
			} else {
				map2DGraphics.setColor(Color.WHITE);
			}
			//Editor.log("Roomdef " + roomDef.getName());
			Vector polygons = roomDef.createPolygons();
			int polygonCount = polygons.size();
			for (int j = 0; j < polygonCount; j++) {
				BSPPolygon bspPolygon = (BSPPolygon) polygons.elementAt(j);
				if (bspPolygon.isWall()) {
					//Editor.log(bspPolygon.getLine().x1 + " " + bspPolygon.getLine().y1 + " " + bspPolygon.getLine().x2 + " " + bspPolygon.getLine().y2);
					int x1 = (int) ((bspPolygon.getLine().x1 + shiftX) / zoomFactorX);
					int y1 = (int) ((bspPolygon.getLine().y1 + shiftY) / zoomFactorY);
					int x2 = (int) ((bspPolygon.getLine().x2 + shiftX) / zoomFactorX);
					int y2 = (int) ((bspPolygon.getLine().y2 + shiftY) / zoomFactorY);
					map2DGraphics.drawLine(x1, y1, x2, y2);
				}
			}
			
			Vector vertices = roomDef.getWallVertices();
			int size = vertices.size();
			for (int j = 0; j < size; j++) {
				RoomDef.Vertex vertex = (RoomDef.Vertex) vertices.elementAt(j);
				if (editor.getSelectedMapObject() == vertex) {
					//Editor.log(bspPolygon.getLine().x1 + " " + bspPolygon.getLine().y1 + " " + bspPolygon.getLine().x2 + " " + bspPolygon.getLine().y2);
					int x = (int) ((vertex.getX() + shiftX) / zoomFactorX);
					int y = (int) ((vertex.getZ() + shiftY) / zoomFactorY);
					//System.out.println(x + " " + y);
					map2DGraphics.setColor(Color.RED);
					map2DGraphics.fillRect(x - 2, y - 2, 4, 4);
				}
			}
		}
	}

	public void drawPlayer() {
		EditorEngine engine = editor.getEngine();
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
		map2DGraphics.setColor(Color.ORANGE);
		map2DGraphics.drawLine(x, y, x2, y2);
	}
	
	public void drawObjects() {
		EditorEngine engine = editor.getEngine();
		MapLoader mapLoader = engine.getLoader();
		Vector objects = mapLoader.getObjectsInMap();
		int objectCount = objects.size();
		for (int i = 0; i < objectCount; i++) {
			PolygonGroup mapObject = (PolygonGroup) objects.elementAt(i);
			int x = (int) ((mapObject.getTransform().getLocation().x + shiftX) / zoomFactorX);
			int y = (int) ((mapObject.getTransform().getLocation().z + shiftY) / zoomFactorY);
			//System.out.println(x + " " + y);
			if (editor.getSelectedMapObject() == mapObject) {
				map2DGraphics.setColor(Color.RED);
			} else {
				map2DGraphics.setColor(Color.GREEN);
			}
			map2DGraphics.fillRect(x - 2, y - 2, 4, 4);
		}
	}
	
	public void drawLights() {
		EditorEngine engine = editor.getEngine();
		MapLoader mapLoader = engine.getLoader();
		Vector lights = mapLoader.getLights();
		int objectCount = lights.size();
		for (int i = 0; i < objectCount; i++) {
			PointLight3D light = (PointLight3D) lights.elementAt(i);
			int x = (int) ((light.x + shiftX) / zoomFactorX);
			int y = (int) ((light.z + shiftY) / zoomFactorY);
			//System.out.println(x + " " + y);
			if (editor.getSelectedMapObject() == light) {
				map2DGraphics.setColor(Color.RED);
			} else {
				map2DGraphics.setColor(Color.YELLOW);
			}
			map2DGraphics.fillRect(x - 1, y - 1, 2, 2);
		}
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
		drawObjects();
		drawLights();

		g.drawImage(map2DImage, 0, 0, null);

		//		g.setFont(Font.getFont(Font.MONOSPACED));
		//		g.setColor(Color.WHITE);
		//		g.drawString("2D Map", 50, 50);
	}

	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e) {
		
		float playerX = (e.getX() * zoomFactorX) - shiftX;
		float playerZ = (e.getY() * zoomFactorY) - shiftY;
		
		Vector3D playerLocation = editor.getEngine().getGameObjectManager().getPlayer().getLocation();
		playerLocation.x = playerX;
		playerLocation.z = playerZ;
		
		editor.updateUI();
		
	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}

class EditorToolkit extends AWTToolkit {

	private InputManager inputManager;
	private EditorScreen awtScreen;
	private Editor editor;
	private ResourceLoader resourceLoader;

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
	
	@Override
	public ResourceLoader getResourceLoader() {
		if (resourceLoader == null) {
			resourceLoader = new ResourceLoaderSE();
		}
		return resourceLoader;
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
	private static String ADD_ROOM_COMMAND = "addRoom";
	private static String COPY_VERTEX_COMMAND = "addVertex";
	private static String REMOVE_COMMAND = "remove";
	private static String CLEAR_COMMAND = "clear";

	private DynamicTree treePanel;
	private Editor editor;

	public MapInspector(Editor editor, int panelWidth, int panelHeight) {
		super(new BorderLayout());
		this.editor = editor;

		//Create the components.
		treePanel = new DynamicTree();
		populateTree();

		JButton addRoomButton = new JButton("Add Room");
		addRoomButton.setActionCommand(ADD_ROOM_COMMAND);
		addRoomButton.addActionListener(this);
		
		JButton addVertexButton = new JButton("Copy Vertex");
		addVertexButton.setActionCommand(COPY_VERTEX_COMMAND);
		addVertexButton.addActionListener(this);

		JButton removeButton = new JButton("Remove");
		removeButton.setActionCommand(REMOVE_COMMAND);
		removeButton.addActionListener(this);

//		JButton clearButton = new JButton("Clear");
//		clearButton.setActionCommand(CLEAR_COMMAND);
//		clearButton.addActionListener(this);

		//Lay everything out.
		this.setPreferredSize(new Dimension(panelWidth, panelHeight));
		add(treePanel, BorderLayout.CENTER);

		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(addRoomButton);
		panel.add(addVertexButton);
		panel.add(removeButton);
//		panel.add(clearButton);
		add(panel, BorderLayout.LINE_END);
	}
	
	private DefaultMutableTreeNode addRoomDef(RoomDef roomDef) {
		
		DefaultMutableTreeNode roomNode = treePanel.addObject(null, roomDef);
		
		RoomAmbientLightIntensity roomAmbientLightIntensity = new RoomAmbientLightIntensity(roomDef);
		treePanel.addObject(roomNode, roomAmbientLightIntensity);
		
		Floor floor = roomDef.getFloor();
		treePanel.addObject(roomNode, floor);
		Ceil ceil = roomDef.getCeil();
		treePanel.addObject(roomNode, ceil);
		
		Vector wallVertices = roomDef.getWallVertices();
		int wallVertexCount = wallVertices.size();
		for (int j = 0; j < wallVertexCount; j++) {
			RoomDef.Vertex vertex = (RoomDef.Vertex) wallVertices.elementAt(j);
			treePanel.addObject(roomNode, vertex);
		}
		
		return roomNode;
		
	}

	public void populateTree() {
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
		
		treePanel.clear();
		
		EditorEngine engine = editor.getEngine();
		MapLoader mapLoader = engine.getLoader();
		
		/* Rooms */
		
		Vector rooms = mapLoader.getRooms();
		int roomCount = rooms.size();
		for (int i = 0; i < roomCount; i++) {
			RoomDef roomDef = (RoomDef) rooms.elementAt(i);
			addRoomDef(roomDef);
		}
		
		
		/* Objects */
		
		Vector objectList = mapLoader.getObjectsInMap();
		int size = objectList.size();
		for (int i = 0; i < size; i++) {
			PolygonGroup polygonGroup = (PolygonGroup) objectList.elementAt(i);
			DefaultMutableTreeNode node = treePanel.addObject(null, polygonGroup);
		}
		
		/* Lights */
		
		Vector lightList = mapLoader.getLights();
		size = lightList.size();
		for (int i = 0; i < size; i++) {
			PointLight3D light = (PointLight3D) lightList.elementAt(i);
			DefaultMutableTreeNode node = treePanel.addObject(null, light);
		}
		
		 
		
		
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if (ADD_ROOM_COMMAND.equals(command)) {
			//Add button clicked
			//treePanel.addObject("New Node " + newNodeSuffix++);
			EditorEngine engine = editor.getEngine();
			MapLoader mapLoader = engine.getLoader();
			
			// Look for a a default material
			Enumeration enumeration = mapLoader.getMaterials().elements();
			if (!enumeration.hasMoreElements()) {
				return;
			}
			Material material = null;
			while(enumeration.hasMoreElements()) {
				material = (Material)enumeration.nextElement();
				if (material.texture != null) {
					break;
				}
			}
			if (material == null) {
				Editor.log("Can't create new Room because no texture is available");
				return;
			}
			
			// Create the room with the default material at the player location
			RoomDef room  = new RoomDef();
			room.setName("New");
			room.setFloor(10, material);
			room.setCeil(200, material);
			int size = 70;
			Vector3D playerLocation = engine.getGameObjectManager().getPlayer().getLocation();
			room.addVertex(playerLocation.x - size, playerLocation.z - size, material);
			room.addVertex(playerLocation.x - size, playerLocation.z + size, 10, 32, material);
			room.addVertex(playerLocation.x + size, playerLocation.z + size, material);
			room.addVertex(playerLocation.x + size, playerLocation.z - size, material);
			
			Vector rooms = mapLoader.getRooms();
			rooms.addElement(room);
			
			DefaultMutableTreeNode node = addRoomDef(room);
			TreePath path = new TreePath(node.getPath());
			treePanel.tree.setSelectionPath(path);
			treePanel.tree.scrollPathToVisible(path);
			
			editor.notifyMapChanged();
			
		}
		if (COPY_VERTEX_COMMAND.equals(command)) {
			Object selectedObject = getSelectedObject();
			if (!(selectedObject instanceof RoomDef.Vertex)) {
				return;
			}
			
			RoomDef.Vertex vertex = (RoomDef.Vertex) selectedObject;
			RoomDef roomDef = vertex.getRoomDef();
			RoomDef.Vertex newVertex = (RoomDef.Vertex) vertex.clone();
			roomDef.addVertexAfter(newVertex, vertex);
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)getSelectedNode();
			DefaultMutableTreeNode roomDefNode = (DefaultMutableTreeNode)selectedNode.getParent();
			treePanel.addObject(roomDefNode, newVertex, true, roomDefNode.getIndex(selectedNode) + 1);
			
			editor.notifyMapChanged();
		} else if (REMOVE_COMMAND.equals(command)) {
			
			Object selectedObject = getSelectedObject();
			
			if (selectedObject instanceof RoomDef) {
				RoomDef room = (RoomDef) selectedObject;
				EditorEngine engine = editor.getEngine();
				MapLoader mapLoader = engine.getLoader();
				Vector rooms = mapLoader.getRooms();
				rooms.removeElement(room);
				treePanel.removeCurrentNode();
				editor.notifyMapChanged();
			} else if (selectedObject instanceof RoomDef.Vertex) {
				RoomDef.Vertex vertex = (RoomDef.Vertex) selectedObject;
				RoomDef roomDef = vertex.getRoomDef();
				Vector vertices = roomDef.getWallVertices();
				if (vertices.size() > 3) { // We need at least 3 vertices (??)
					roomDef.removeVertex(vertex);
					treePanel.removeCurrentNode();
					editor.notifyMapChanged();
				}
			}
			
			
			
		} else if (CLEAR_COMMAND.equals(command)) {
			//Clear button clicked.
			treePanel.clear();
		}
	}
	
	public DefaultMutableTreeNode getSelectedNode() {
		return treePanel.getSelectedNode();
	}
	
	public Object getSelectedObject() {
		return treePanel.getSelectedObject();
	}
	
	public Object getSelectedObjectParent() {
		return treePanel.getSelectedObjectParent();
	}
	
	
	class DynamicTree extends JPanel implements TreeSelectionListener {
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
			tree.addTreeSelectionListener(this);

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
			if (parent == null) {
				parent = rootNode;
			}
			return addObject(parent, child, shouldBeVisible, parent.getChildCount());
		}
		
		public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child, boolean shouldBeVisible, int index) {
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);

			if (parent == null) {
				parent = rootNode;
			}

			treeModel.insertNodeInto(childNode, parent, index);

			//Make sure the user can see the lovely new node.
			if (shouldBeVisible) {
				tree.scrollPathToVisible(new TreePath(childNode.getPath()));
			}
			return childNode;
		}
		
		
		public DefaultMutableTreeNode getSelectedNode() {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) (tree.getSelectionPath().getLastPathComponent());
			return node;
		}
		
		public Object getSelectedObject() {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) (tree.getSelectionPath().getLastPathComponent());
			if (node == null) {
				return null;
			}
			return node.getUserObject();
		}
		
		public Object getSelectedObjectParent() {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) (tree.getSelectionPath().getLastPathComponent());
			if (node == null) {
				return null;
			}
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)node.getParent();
			if (parentNode == null) {
				return null;
			}
			return parentNode.getUserObject();
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

		/* TreeSelectionListener interface */
		
		public void valueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) (e.getPath().getLastPathComponent());
			Object mapObject = node.getUserObject();
			editor.setSelectedMapObject(mapObject);
		}
	}
	
}

class RoomAmbientLightIntensity {
	
	private RoomDef roomDef;
	
	public RoomAmbientLightIntensity(RoomDef roomDef) {
		this.roomDef = roomDef;
	}
	
	
	public RoomDef getRoomDef() {
		return roomDef;
	}

	@Override
	public String toString() {
		return "Ambient light (" + roomDef.getAmbientLightIntensity() + ")";
	}
	
}

class ObjectInspector extends JPanel {

	private Editor editor;
	private JPanel currentObjectPanel;
	
	public ObjectInspector(Editor editor, int panelWidth, int panelHeight) {
		this.editor = editor;
		setLayout(new BorderLayout());
		setVisible(false);
		//setPreferredSize(new Dimension(panelWidth, panelHeight));	
		//roomDefPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));	
		
	}
	
	public void setMapObject(Object mapObject) {
		Editor.log("ObjectInspector: setMapObject");
		if (mapObject instanceof RoomDef) {
			currentObjectPanel = new RoomDefPanel((RoomDef)mapObject);
			removeAll();
			add(currentObjectPanel, BorderLayout.CENTER);
			setVisible(true);
			revalidate();
		} else if (mapObject instanceof RoomDef.Vertex) {
			currentObjectPanel = new RoomDefVertexPanel((RoomDef.Vertex)mapObject);
			removeAll();
			add(currentObjectPanel, BorderLayout.CENTER);
			setVisible(true);
			revalidate();
		} else if (mapObject instanceof RoomDef.HorizontalAreaDef) {
			currentObjectPanel = new HorizontalAreaDefPanel((RoomDef.HorizontalAreaDef)mapObject);
			removeAll();
			add(currentObjectPanel, BorderLayout.CENTER);
			setVisible(true);
			revalidate();
		} else if (mapObject instanceof RoomAmbientLightIntensity) {
			currentObjectPanel = new AmbientLightPanel((RoomAmbientLightIntensity)mapObject);
			removeAll();
			add(currentObjectPanel, BorderLayout.CENTER);
			setVisible(true);
			revalidate();
		}  else if (mapObject instanceof PolygonGroup) {
			currentObjectPanel = new GameObjectPanel((PolygonGroup)mapObject);
			removeAll();
			add(currentObjectPanel, BorderLayout.CENTER);
			setVisible(true);
			revalidate();
		} else if (mapObject instanceof PointLight3D) {
			currentObjectPanel = new PointLightPanel((PointLight3D)mapObject);
			removeAll();
			add(currentObjectPanel, BorderLayout.CENTER);
			setVisible(true);
			revalidate();
		} else {
			setVisible(false);
		}
		
	}
	
	@Override
	public void repaint() { // FIXME we shouldn't override repaint ?
		super.repaint();
		if (currentObjectPanel != null) {
			currentObjectPanel.repaint();
		}
	}
	
	class RoomDefPanel extends JPanel {
		
		private RoomDef roomDef;
		private JPanel vertexPanel;
		
		RoomDefPanel(final RoomDef roomDef) {
			
			this.roomDef = roomDef;
			setLayout(new BorderLayout());
			
			/* Build buttons */
			
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			JLabel moveLabel = new JLabel("Name");
			final JTextField nameField = new JTextField(roomDef.getName(), 30);
			
			ActionListener actionListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					roomDef.setName(nameField.getText());
					editor.notifyMapChanged();
				}
			};
			
			nameField.addActionListener(actionListener);
			buttonPanel.add(moveLabel);
			buttonPanel.add(nameField);
			add(buttonPanel, BorderLayout.NORTH);
			
			vertexPanel = new JPanel();
			vertexPanel.setLayout(new BoxLayout(vertexPanel, BoxLayout.Y_AXIS));
			ScrollPane scrollPane = new ScrollPane();
			scrollPane.add(vertexPanel);
			add(scrollPane, BorderLayout.CENTER);
			
			updateRoomDef(roomDef);
		}
		
		
		
		@Override
		public void repaint() {
			super.repaint();
			if (roomDef != null) {
				updateRoomDef(roomDef);
			}
		}



		private void updateRoomDef(final RoomDef roomDef) {
			
			vertexPanel.removeAll();
			
			Vector wallVertices = roomDef.getWallVertices();
			int wallVertexCount = wallVertices.size();
			
//			JLabel nameLabel = new JLabel(roomDef.getName());
//			add(nameLabel, BorderLayout.NORTH);

			
			for (int j = 0; j < wallVertexCount; j++) {
				final RoomDef.Vertex vertex = (RoomDef.Vertex) wallVertices.elementAt(j);
				
				JPanel linePanel = new JPanel();
				linePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
				// Spinner for X
				JLabel xLabel = new JLabel("X");
				linePanel.add(xLabel);
				SpinnerNumberModel model = new SpinnerNumberModel();
				model.setValue(vertex.getX());
				model.setStepSize(1);				
				JSpinner spinner = new JSpinner(model);
				spinner.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						JSpinner mySpinner = (JSpinner)(e.getSource());
						SpinnerNumberModel myModel = (SpinnerNumberModel)(mySpinner.getModel());
						roomDef.setVertexX(vertex, Float.valueOf((Float)myModel.getValue()));
			            Editor.log("spinner value changed: " +  myModel.getValue());
			            editor.notifyMapChanged();
					}
				});
				linePanel.add(spinner);
				// Spinner for Z
				JLabel zLabel = new JLabel("Z");
				linePanel.add(zLabel);
				model = new SpinnerNumberModel();
				model.setValue(vertex.getZ());
				model.setStepSize(1);				
				spinner = new JSpinner(model);
				spinner.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						JSpinner mySpinner = (JSpinner)(e.getSource());
						SpinnerNumberModel myModel = (SpinnerNumberModel)(mySpinner.getModel());
						roomDef.setVertexZ(vertex, Float.valueOf((Float)myModel.getValue()));
			            Editor.log("spinner value changed: " +  myModel.getValue());
			            editor.notifyMapChanged();
					}
				});
				linePanel.add(spinner);
				// Spinner for Bottom
				JLabel bottomLabel = new JLabel("Bottom");
				linePanel.add(bottomLabel);
				model = new SpinnerNumberModel();
				model.setValue(vertex.getBottom());
				model.setStepSize(1);				
				spinner = new JSpinner(model);
				spinner.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						JSpinner mySpinner = (JSpinner)(e.getSource());
						SpinnerNumberModel myModel = (SpinnerNumberModel)(mySpinner.getModel());
						roomDef.setVertexBottom(vertex, Float.valueOf((Float)myModel.getValue()));
			            Editor.log("spinner value changed: " +  myModel.getValue());
			            editor.notifyMapChanged();
					}
				});
				linePanel.add(spinner);
				// Spinner for Top
				JLabel topLabel = new JLabel("Top");
				linePanel.add(topLabel);
				model = new SpinnerNumberModel();
				model.setValue(vertex.getTop());
				model.setStepSize(1);				
				spinner = new JSpinner(model);
				spinner.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						JSpinner mySpinner = (JSpinner)(e.getSource());
						SpinnerNumberModel myModel = (SpinnerNumberModel)(mySpinner.getModel());
						roomDef.setVertexTop(vertex, Float.valueOf((Float)myModel.getValue()));
			            Editor.log("spinner value changed: " +  myModel.getValue());
			            editor.notifyMapChanged();
					}
				});
				linePanel.add(spinner);
				
				vertexPanel.add(linePanel);
				
				revalidate();
			}
			
			
		}
		
//		@Override
//		public void paintComponent(Graphics g) {
//			super.paintComponent(g);
//			
//			Editor.log("RoomDefPanel: paintComponent");
//			
//			Vector wallVertices = roomDef.getWallVertices();
//			int wallVertexCount = wallVertices.size();
//			int height = g.getFontMetrics().getHeight();
//			g.drawString(roomDef.getName(), 0, height);
//			for (int j = 0; j < wallVertexCount; j++) {
//				RoomDef.Vertex vertex = (RoomDef.Vertex) wallVertices.elementAt(j);
//				g.drawString(vertex.toString(), 0, height * (j + 3));
//			}
//			
//		}
		
	}
	
	class RoomDefVertexPanel extends JPanel {
		
		private RoomDef.Vertex vertex;
		
		RoomDefVertexPanel(final RoomDef.Vertex vertex) {
			this.vertex = vertex;
			setLayout(new BorderLayout());
		}
		
		@Override
		public void repaint() {
			super.repaint();
			if (vertex != null) {
				removeAll();
				updateMaterialPanel(vertex);
				updateVertexPanel(vertex);
				revalidate();
			}
		}
		
		private void updateMaterialPanel(RoomDef.Vertex vertex) {
			MaterialPanel materialPanel = new MaterialPanel(vertex.getMaterial());
			materialPanel.setBorder(new LineBorder(Color.GRAY));
			add(materialPanel, BorderLayout.CENTER);
		}
		

		private void updateVertexPanel(final RoomDef.Vertex vertex) {
			
			final RoomDef roomDef = vertex.getRoomDef();

			JPanel vertexPanel = new JPanel();
			vertexPanel.setLayout(new BoxLayout(vertexPanel, BoxLayout.Y_AXIS));
			vertexPanel.setBorder(new LineBorder(Color.GRAY));
			vertexPanel.removeAll();

			JPanel linePanel = new JPanel();
			linePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			// Spinner for X
			JLabel xLabel = new JLabel("X");
			linePanel.add(xLabel);
			SpinnerNumberModel model = new SpinnerNumberModel();
			model.setValue(vertex.getX());
			model.setStepSize(1);
			JSpinner spinner = new JSpinner(model);
			spinner.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSpinner mySpinner = (JSpinner) (e.getSource());
					SpinnerNumberModel myModel = (SpinnerNumberModel) (mySpinner.getModel());
					roomDef.setVertexX(vertex, Float.valueOf((Float) myModel.getValue()));
					Editor.log("spinner value changed: " + myModel.getValue());
					editor.notifyMapChanged();
				}
			});
			linePanel.add(spinner);
			// Spinner for Z
			JLabel yLabel = new JLabel("Y");
			linePanel.add(yLabel);
			model = new SpinnerNumberModel();
			model.setValue(vertex.getZ());
			model.setStepSize(1);
			spinner = new JSpinner(model);
			spinner.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSpinner mySpinner = (JSpinner) (e.getSource());
					SpinnerNumberModel myModel = (SpinnerNumberModel) (mySpinner.getModel());
					roomDef.setVertexZ(vertex, Float.valueOf((Float) myModel.getValue()));
					Editor.log("spinner value changed: " + myModel.getValue());
					editor.notifyMapChanged();
				}
			});
			linePanel.add(spinner);
			// Spinner for Bottom
			JLabel bottomLabel = new JLabel("Bottom");
			linePanel.add(bottomLabel);
			model = new SpinnerNumberModel();
			model.setValue(vertex.getBottom());
			model.setStepSize(1);
			spinner = new JSpinner(model);
			spinner.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSpinner mySpinner = (JSpinner) (e.getSource());
					SpinnerNumberModel myModel = (SpinnerNumberModel) (mySpinner.getModel());
					roomDef.setVertexBottom(vertex, Float.valueOf((Float) myModel.getValue()));
					Editor.log("spinner value changed: " + myModel.getValue());
					editor.notifyMapChanged();
				}
			});
			linePanel.add(spinner);
			// Spinner for Top
			JLabel topLabel = new JLabel("Top");
			linePanel.add(topLabel);
			model = new SpinnerNumberModel();
			model.setValue(vertex.getTop());
			model.setStepSize(1);
			spinner = new JSpinner(model);
			spinner.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSpinner mySpinner = (JSpinner) (e.getSource());
					SpinnerNumberModel myModel = (SpinnerNumberModel) (mySpinner.getModel());
					roomDef.setVertexTop(vertex, Float.valueOf((Float) myModel.getValue()));
					Editor.log("spinner value changed: " + myModel.getValue());
					editor.notifyMapChanged();
				}
			});
			linePanel.add(spinner);

			vertexPanel.add(linePanel);
			add(vertexPanel, BorderLayout.NORTH);

			revalidate();

		}
		
	}
	
	class HorizontalAreaDefPanel extends JPanel {
		
		private HorizontalAreaDef horizontalArea;
		
		HorizontalAreaDefPanel(final HorizontalAreaDef horizontalArea) {
			this.horizontalArea = horizontalArea;
			setLayout(new BorderLayout());
		}
		
		@Override
		public void repaint() {
			super.repaint();
			if (horizontalArea != null) {
				removeAll();
				updateMaterialPanel(horizontalArea);
				updateVertexPanel(horizontalArea);
				revalidate();
			}
		}
		
		private void updateMaterialPanel(HorizontalAreaDef horizontalArea) {
			MaterialPanel materialPanel = new MaterialPanel(horizontalArea.getMaterial());
			materialPanel.setBorder(new LineBorder(Color.GRAY));
			add(materialPanel, BorderLayout.CENTER);
		}
		
		private void updateVertexPanel(final HorizontalAreaDef horizontalArea) {
			
			final RoomDef roomDef = horizontalArea.getRoomDef();

			JPanel vertexPanel = new JPanel();
			vertexPanel.setLayout(new BoxLayout(vertexPanel, BoxLayout.Y_AXIS));
			vertexPanel.setBorder(new LineBorder(Color.GRAY));
			vertexPanel.removeAll();

			JPanel linePanel = new JPanel();
			linePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			// Spinner for height
			SpinnerNumberModel model = new SpinnerNumberModel();
			model.setValue(horizontalArea.getHeight());
			model.setStepSize(1);
			JLabel heightLabel = new JLabel("Height");
			linePanel.add(heightLabel);
			JSpinner spinner = new JSpinner(model);
			spinner.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSpinner mySpinner = (JSpinner) (e.getSource());
					SpinnerNumberModel myModel = (SpinnerNumberModel) (mySpinner.getModel());
					roomDef.setHorizontalAreaHeight(horizontalArea, Float.valueOf((Float) myModel.getValue()));
					Editor.log("spinner value changed: " + myModel.getValue());
					editor.notifyMapChanged();
				}
			});
			linePanel.add(spinner);
			vertexPanel.add(linePanel);
			add(vertexPanel, BorderLayout.NORTH);

			revalidate();

		}
		
	}
	
	class AmbientLightPanel extends JPanel {
		
		private RoomAmbientLightIntensity intensity;
		
		AmbientLightPanel(RoomAmbientLightIntensity intensity) {
			this.intensity = intensity;
			setLayout(new BorderLayout());
		}
		
		@Override
		public void repaint() {
			super.repaint();
			if (intensity != null) {
				removeAll();
				updateAmbientLightPanel(intensity);
				revalidate();
			}
		}
		
		private void updateAmbientLightPanel(final RoomAmbientLightIntensity intensity) {

			JPanel vertexPanel = new JPanel();
			vertexPanel.setLayout(new BoxLayout(vertexPanel, BoxLayout.Y_AXIS));
			vertexPanel.setBorder(new LineBorder(Color.GRAY));

			JPanel linePanel = new JPanel();
			linePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			// Spinner for height
			SpinnerNumberModel model = new SpinnerNumberModel();
			model.setValue(intensity.getRoomDef().getAmbientLightIntensity());
//			model.setMinimum(0);
//			model.setMaximum(1);
			model.setStepSize(0.1);
			JLabel heightLabel = new JLabel("Intensity");
			linePanel.add(heightLabel);
			JSpinner spinner = new JSpinner(model);
			spinner.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSpinner mySpinner = (JSpinner) (e.getSource());
					SpinnerNumberModel myModel = (SpinnerNumberModel) (mySpinner.getModel());
					intensity.getRoomDef().setAmbientLightIntensity(Float.valueOf((Float) myModel.getValue()));
					Editor.log("spinner value changed: " + myModel.getValue());
					editor.notifyMapChanged();
				}
			});
			linePanel.add(spinner);
			vertexPanel.add(linePanel);
			add(vertexPanel, BorderLayout.CENTER);

		}
		
	}
	
	class GameObjectPanel extends JPanel {
		
		private PolygonGroup polygonGroup;
		
		GameObjectPanel(PolygonGroup polygonGroup) {
			this.polygonGroup = polygonGroup;
			setLayout(new BorderLayout());
		}
		
		@Override
		public void repaint() {
			super.repaint();
			if (polygonGroup != null) {
				removeAll();
				//updatePicturePanel(polygonGroup);
				updateInfoPanel(polygonGroup);
				revalidate();
			}
		}
		
//		private void updatePicturePanel(PolygonGroup polygonGroup) {
//			MaterialPanel materialPanel = new MaterialPanel(horizontalArea.getMaterial());
//			materialPanel.setBorder(new LineBorder(Color.GRAY));
//			add(materialPanel, BorderLayout.CENTER);
//		}
		
		private void updateInfoPanel(final PolygonGroup polygonGroup) {

			JPanel infoPanel = new JPanel();
			infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
			infoPanel.setBorder(new LineBorder(Color.GRAY));
			
			final Vector3D location = polygonGroup.getTransform().getLocation();
			
			/* Name */

			JPanel linePanel = new JPanel();
			linePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			JLabel nameLabel = new JLabel("Name");
			final JTextField nameField = new JTextField(polygonGroup.getName(), 10);
			ActionListener actionListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					polygonGroup.setName(nameField.getText());
					editor.notifyMapChanged();
				}
			};
			nameField.addActionListener(actionListener);
			linePanel.add(nameLabel);
			linePanel.add(nameField);
			infoPanel.add(linePanel);
			
			/* Filename */
			
			linePanel = new JPanel();
			linePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			JLabel fileNameLabel = new JLabel("File: " + polygonGroup.getFilename());
			linePanel.add(fileNameLabel);
			infoPanel.add(linePanel);
			
			/* Coordinates */
			
			linePanel = new JPanel();
			linePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			// Spinner for X
			SpinnerNumberModel model = new SpinnerNumberModel();
			model.setValue(location.x);
			model.setStepSize(1);
			JLabel label = new JLabel("X");
			linePanel.add(label);
			JSpinner spinner = new JSpinner(model);
			spinner.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSpinner mySpinner = (JSpinner) (e.getSource());
					SpinnerNumberModel myModel = (SpinnerNumberModel) (mySpinner.getModel());
					location.x = Float.valueOf((Float) myModel.getValue());
					Editor.log("spinner value changed: " + myModel.getValue());
					editor.notifyMapChanged();
				}
			});
			linePanel.add(spinner);
			// Spinner for Y
			model = new SpinnerNumberModel();
			model.setValue(location.y);
			model.setStepSize(1);
			label = new JLabel("Y");
			linePanel.add(label);
			spinner = new JSpinner(model);
			spinner.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSpinner mySpinner = (JSpinner) (e.getSource());
					SpinnerNumberModel myModel = (SpinnerNumberModel) (mySpinner.getModel());
					location.y = Float.valueOf((Float) myModel.getValue());
					Editor.log("spinner value changed: " + myModel.getValue());
					editor.notifyMapChanged();
				}
			});
			linePanel.add(spinner);
			// Spinner for Z
			model = new SpinnerNumberModel();
			model.setValue(location.z);
			model.setStepSize(1);
			label = new JLabel("Z");
			linePanel.add(label);
			spinner = new JSpinner(model);
			spinner.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSpinner mySpinner = (JSpinner) (e.getSource());
					SpinnerNumberModel myModel = (SpinnerNumberModel) (mySpinner.getModel());
					location.z = Float.valueOf((Float) myModel.getValue());
					Editor.log("spinner value changed: " + myModel.getValue());
					editor.notifyMapChanged();
				}
			});
			linePanel.add(spinner);
			// Spinner for angle
			model = new SpinnerNumberModel();
			model.setValue(polygonGroup.getTransform().getAngleY() * 180 / Math.PI);
			model.setStepSize(1);
			label = new JLabel("Angle");
			linePanel.add(label);
			spinner = new JSpinner(model);
			spinner.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSpinner mySpinner = (JSpinner) (e.getSource());
					SpinnerNumberModel myModel = (SpinnerNumberModel) (mySpinner.getModel());
					float angle = (float) (((Double) myModel.getValue()) / 180.0f * Math.PI);
					polygonGroup.getTransform().setAngleY(angle);
					Editor.log("spinner value changed: " + angle);
					editor.notifyMapChanged();
				}
			});
			linePanel.add(spinner);
			
			infoPanel.add(linePanel);
			add(infoPanel, BorderLayout.NORTH);


		}
		
	}
	
	class PointLightPanel extends JPanel {
		
		private PointLight3D pointLight;
		
		PointLightPanel(PointLight3D pointLight) {
			this.pointLight = pointLight;
			setLayout(new BorderLayout());
		}
		
		@Override
		public void repaint() {
			super.repaint();
			if (pointLight != null) {
				removeAll();
				updateLightPanel(pointLight);
				revalidate();
			}
		}
		
		private void updateLightPanel(final PointLight3D pointLight) {

			JPanel vertexPanel = new JPanel();
			vertexPanel.setLayout(new BoxLayout(vertexPanel, BoxLayout.Y_AXIS));
			vertexPanel.setBorder(new LineBorder(Color.GRAY));

			/* Coordinates */
			
			JPanel linePanel = new JPanel();
			linePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			// Spinner for X
			SpinnerNumberModel model = new SpinnerNumberModel();
			model.setValue(pointLight.x);
			model.setStepSize(1);
			JLabel label = new JLabel("X");
			linePanel.add(label);
			JSpinner spinner = new JSpinner(model);
			spinner.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSpinner mySpinner = (JSpinner) (e.getSource());
					SpinnerNumberModel myModel = (SpinnerNumberModel) (mySpinner.getModel());
					pointLight.x = Float.valueOf((Float) myModel.getValue());
					Editor.log("spinner value changed: " + myModel.getValue());
					editor.notifyMapChanged();
				}
			});
			linePanel.add(spinner);
			// Spinner for Y
			model = new SpinnerNumberModel();
			model.setValue(pointLight.y);
			model.setStepSize(1);
			label = new JLabel("Y");
			linePanel.add(label);
			spinner = new JSpinner(model);
			spinner.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSpinner mySpinner = (JSpinner) (e.getSource());
					SpinnerNumberModel myModel = (SpinnerNumberModel) (mySpinner.getModel());
					pointLight.y = Float.valueOf((Float) myModel.getValue());
					Editor.log("spinner value changed: " + myModel.getValue());
					editor.notifyMapChanged();
				}
			});
			linePanel.add(spinner);
			// Spinner for Z
			model = new SpinnerNumberModel();
			model.setValue(pointLight.z);
			model.setStepSize(1);
			label = new JLabel("Z");
			linePanel.add(label);
			spinner = new JSpinner(model);
			spinner.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSpinner mySpinner = (JSpinner) (e.getSource());
					SpinnerNumberModel myModel = (SpinnerNumberModel) (mySpinner.getModel());
					pointLight.z = Float.valueOf((Float) myModel.getValue());
					Editor.log("spinner value changed: " + myModel.getValue());
					editor.notifyMapChanged();
				}
			});
			linePanel.add(spinner);
			vertexPanel.add(linePanel);
			
			/* Properties */
			
			linePanel = new JPanel();
			linePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			// Spinner for  intensity
			model = new SpinnerNumberModel();
			model.setValue(pointLight.getIntensity());
			model.setStepSize(0.1);
			label = new JLabel("Intensity");
			linePanel.add(label);
			spinner = new JSpinner(model);
			spinner.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSpinner mySpinner = (JSpinner) (e.getSource());
					SpinnerNumberModel myModel = (SpinnerNumberModel) (mySpinner.getModel());
					pointLight.setIntensity((Float) myModel.getValue());
					Editor.log("spinner value changed: " + myModel.getValue());
					editor.notifyMapChanged();
				}
			});
			linePanel.add(spinner);
			// Spinner for distance falloff
			model = new SpinnerNumberModel();
			model.setValue(pointLight.getDistanceFalloff());
			model.setStepSize(1);
			label = new JLabel("Distance falloff");
			linePanel.add(label);
			spinner = new JSpinner(model);
			spinner.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSpinner mySpinner = (JSpinner) (e.getSource());
					SpinnerNumberModel myModel = (SpinnerNumberModel) (mySpinner.getModel());
					pointLight.setDistanceFalloff((Float) myModel.getValue());
					Editor.log("spinner value changed: " + myModel.getValue());
					editor.notifyMapChanged();
				}
			});
			linePanel.add(spinner);
			vertexPanel.add(linePanel);
			
			add(vertexPanel, BorderLayout.NORTH);

		}
		
	}

}

class MaterialPanel extends JPanel {
	
	public MaterialPanel(Material material) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel moveLabel = new JLabel("Material");
		final JTextField nameField = new JTextField(material.name +  " (" + material.library + ":" + material.textureFileName + ")", 30);
		nameField.setEditable(false);
		buttonPanel.add(moveLabel);
		buttonPanel.add(nameField);
		add(buttonPanel);
		if (material.texture != null) {
			MaterialImagePanel materialPanel = new MaterialImagePanel(material);
			add(materialPanel);
		}
		
	}

	class MaterialImagePanel extends JPanel {

		private Material material;
		private BufferedImage nativeImage;
		private Dimension dimension;

		//	float zoomFactorX;
		//	float zoomFactorY;

		public MaterialImagePanel(Material material) {
			this.material = material;

			//setFocusable(true);
			int w = material.texture.getWidth();
			int h = material.texture.getHeight();

			this.dimension = new Dimension(w, h);

			InputStream is = getClass().getResourceAsStream("/res/" + material.textureFileName);
			try {
				nativeImage = ImageIO.read(is);
			} catch (IOException e) {
				e.printStackTrace();
			}

			//setLayout(new FlowLayout(FlowLayout.CENTER));

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
			g.drawImage(nativeImage, (getWidth() - nativeImage.getWidth()) / 2, (getHeight() - nativeImage.getHeight()) / 2, null);
		}

	}
}



class ToolsPanel extends JPanel {
	
	private Editor editor;
	private JPanel vertexPanel;
	
	ToolsPanel(final Editor editor) {
		
		this.editor = editor;
		setLayout(new BorderLayout());
		
		/* Build buttons */
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		JPanel linePanel = new JPanel();
		linePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JLabel label = new JLabel("Move");
		final JButton xLessButton = new JButton("-X");
		final JButton xButton = new JButton("X");
		final JButton zLessButton = new JButton("-Z");
		final JButton zButton = new JButton("Z");
		final JButton yLessButton = new JButton("-Y");
		final JButton yButton = new JButton("Y");
		final SpinnerNumberModel moveModel = new SpinnerNumberModel();
		moveModel.setValue(100);
		moveModel.setStepSize(10);
		//model.setMinimum(0);
		JSpinner spinner = new JSpinner(moveModel);
		linePanel.add(label);
		linePanel.add(xLessButton);
		linePanel.add(xButton);
		linePanel.add(zLessButton);
		linePanel.add(zButton);
		linePanel.add(yLessButton);
		linePanel.add(yButton);
		linePanel.add(spinner);
		buttonPanel.add(linePanel);
		
		linePanel = new JPanel();
		linePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		label = new JLabel("Stretch");
		final JButton xLessStretchButton = new JButton("-X");
		final JButton xStretchButton = new JButton("X");
		final JButton zLessStretchButton = new JButton("-Z");
		final JButton zStretchButton = new JButton("Z");
		final JButton yLessStretchButton = new JButton("-Y");
		final JButton yStretchButton = new JButton("Y");
		final SpinnerNumberModel stretchModel = new SpinnerNumberModel();
		stretchModel.setValue(100);
		stretchModel.setStepSize(10);
		//model.setMinimum(0);
		spinner = new JSpinner(stretchModel);
		linePanel.add(label);
		linePanel.add(xLessStretchButton);
		linePanel.add(xStretchButton);
		linePanel.add(zLessStretchButton);
		linePanel.add(zStretchButton);
		linePanel.add(yLessStretchButton);
		linePanel.add(yStretchButton);
		linePanel.add(spinner);
		buttonPanel.add(linePanel);
		
		ActionListener actionListener = new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Object selectedMapObject = editor.getSelectedMapObject();
				if (selectedMapObject instanceof RoomDef) {
					RoomDef roomDef = (RoomDef) selectedMapObject;
					if (e.getSource() == xLessButton) {
						roomDef.moveX(-(Integer) moveModel.getNumber());
					} else if (e.getSource() == xButton) {
						roomDef.moveX((Integer) moveModel.getNumber());
					} else if (e.getSource() == zLessButton) {
						roomDef.moveZ(-(Integer) moveModel.getNumber());
					} else if (e.getSource() == zButton) {
						roomDef.moveZ((Integer) moveModel.getNumber());
					} else if (e.getSource() == yLessButton) {
						roomDef.moveY(-(Integer) moveModel.getNumber());
					} else if (e.getSource() == yButton) {
						roomDef.moveY((Integer) moveModel.getNumber());
					} else if (e.getSource() == xLessStretchButton) {
						roomDef.stretchX(-(Integer) stretchModel.getNumber());
					} else if (e.getSource() == xStretchButton) {
						roomDef.stretchX((Integer) stretchModel.getNumber());
					} else if (e.getSource() == zLessStretchButton) {
						roomDef.stretchZ(-(Integer) stretchModel.getNumber());
					} else if (e.getSource() == zStretchButton) {
						roomDef.stretchZ((Integer) stretchModel.getNumber());
					} else if (e.getSource() == yLessStretchButton) {
						roomDef.stretchY(-(Integer) stretchModel.getNumber());
					} else if (e.getSource() == yStretchButton) {
						roomDef.stretchY((Integer) stretchModel.getNumber());
					}
				} else if (selectedMapObject instanceof RoomDef.Vertex) {
					RoomDef.Vertex v = (RoomDef.Vertex) selectedMapObject;
					RoomDef roomDef = v.getRoomDef();
					int value = (Integer) moveModel.getNumber();
					if (e.getSource() == xLessButton) {
						roomDef.setVertexX(v, v.getX() - value); 
					} else if (e.getSource() == xButton) {
						roomDef.setVertexX(v, v.getX() + value);
					} else if (e.getSource() == zLessButton) {
						roomDef.setVertexZ(v, v.getZ() - value);
					} else if (e.getSource() == zButton) {
						roomDef.setVertexZ(v, v.getZ() + value);
					} else if (e.getSource() == yLessButton) {
						roomDef.setVertexBottom(v, v.getBottom() - value);
						roomDef.setVertexTop(v, v.getTop() - value);
					} else if (e.getSource() == yButton) {
						roomDef.setVertexBottom(v, v.getBottom() + value);
						roomDef.setVertexTop(v, v.getTop() + value);
					}
				} else if (selectedMapObject instanceof RoomDef.HorizontalAreaDef) {
					RoomDef.HorizontalAreaDef h = (RoomDef.HorizontalAreaDef) selectedMapObject;
					RoomDef roomDef = h.getRoomDef();
					int value = (Integer) moveModel.getNumber();
					if (e.getSource() == yLessButton) {
						roomDef.setHorizontalAreaHeight(h, h.getHeight() - value);
					} else if (e.getSource() == yButton) {
						roomDef.setHorizontalAreaHeight(h, h.getHeight() + value);
					}
				} else if (selectedMapObject instanceof PolygonGroup) {
					PolygonGroup p = (PolygonGroup) selectedMapObject;
					Vector3D location = p.getTransform().getLocation();
					int value = (Integer) moveModel.getNumber();
					if (e.getSource() == xLessButton) {
						location.x -= value;
					} else if (e.getSource() == xButton) {
						location.x += value;
					} else if (e.getSource() == zLessButton) {
						location.z -= value;
					} else if (e.getSource() == zButton) {
						location.z += value;
					} else if (e.getSource() == yLessButton) {
						location.y -= value;
					} else if (e.getSource() == yButton) {
						location.y += value;
					}
				} else if (selectedMapObject instanceof PointLight3D) {
					PointLight3D p = (PointLight3D) selectedMapObject;
					int value = (Integer) moveModel.getNumber();
					if (e.getSource() == xLessButton) {
						p.x -= value;
					} else if (e.getSource() == xButton) {
						p.x += value;
					} else if (e.getSource() == zLessButton) {
						p.z -= value;
					} else if (e.getSource() == zButton) {
						p.z += value;
					} else if (e.getSource() == yLessButton) {
						p.y -= value;
					} else if (e.getSource() == yButton) {
						p.y += value;
					}
				} 

				editor.notifyMapChanged();
			}
		};
		
		xLessButton.addActionListener(actionListener);
		xButton.addActionListener(actionListener);
		zLessButton.addActionListener(actionListener);
		zButton.addActionListener(actionListener);
		yLessButton.addActionListener(actionListener);
		yButton.addActionListener(actionListener);
		xLessStretchButton.addActionListener(actionListener);
		xStretchButton.addActionListener(actionListener);
		zLessStretchButton.addActionListener(actionListener);
		zStretchButton.addActionListener(actionListener);
		yLessStretchButton.addActionListener(actionListener);
		yStretchButton.addActionListener(actionListener);
		
		add(buttonPanel, BorderLayout.NORTH);
		
		vertexPanel = new JPanel();
		vertexPanel.setLayout(new BoxLayout(vertexPanel, BoxLayout.Y_AXIS));
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.add(vertexPanel);
		add(scrollPane, BorderLayout.CENTER);
		
	}
		
		
}

class ResourceBrowser extends JTabbedPane {
	
	private Editor editor;
	private MaterialsPanel materialsPanel;
	private File[] materialFiles;
	private Material[] materials;
	private MapLoader mapLoader;
	
	public ResourceBrowser(Editor editor) {
		this.editor = editor;
		this.materialsPanel = new MaterialsPanel();
		
		EditorEngine engine = editor.getEngine();
		this.mapLoader = engine.getLoader();
		
		addTab("Textures", materialsPanel);
		addTab("Objects", new JPanel());
	}
	
	class MaterialsPanel extends JPanel implements ActionListener {
		
		private JComboBox materialList;
		private ScrollPane textureScrollPane;
		private JPanel texturesPanel;
		
		public MaterialsPanel() {
			setLayout(new BorderLayout());
			texturesPanel = new JPanel();
			texturesPanel.setLayout(new BoxLayout(texturesPanel, BoxLayout.Y_AXIS));
			textureScrollPane = new ScrollPane();
			textureScrollPane.add(texturesPanel);
			add(textureScrollPane, BorderLayout.CENTER);
			materialList = new JComboBox();
			materialList.addActionListener(MaterialsPanel.this);
			add(materialList, BorderLayout.NORTH);
			rebuildMaterialList();
			materialList.setSelectedIndex(0);
			rebuildMaterialPanel(materialFiles[0]);
		}
		
		public void rebuildMaterialList() {

			URL resourceDirURL = getClass().getResource("/res/");

			File resDir;
			try {
				resDir = new File(resourceDirURL.toURI());
				if (resDir == null) {
					Editor.log("Can't find the material directory");
					return;
				}
			} catch (URISyntaxException e) {
				e.printStackTrace();
				return;
			}

			materialFiles = resDir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					if (name.endsWith(".mtl")) {
						return true;
					}
					return false;
				}
			});

			if (materialFiles != null) {

				//String[] fileStrings = new String[materialFiles.length];
				for (int i = 0; i < materialFiles.length; i++) {
					//fileStrings[i] = materialFiles[i].getName();
					materialList.addItem(materialFiles[i].getName());
				}

			}			

		}
		
		public void rebuildMaterialPanel(File materialFile) {
			ObjectLoader loader = new ObjectLoader(Toolkit.getInstance().getResourceLoader());
			try {
				loader.loadObject(materialFile.getName());
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			
			Hashtable materials = loader.getMaterials();
			Enumeration e = materials.elements();
			texturesPanel.removeAll();
			while(e.hasMoreElements()) {
				Material material = (Material) e.nextElement();
				material.library = materialFile.getName(); // Set library name manually because it can't be done by ObjectLoader
				MaterialPanel materialPanel = new MaterialPanel(material);
				texturesPanel.add(materialPanel);
			}
			texturesPanel.revalidate();
			
			
		}
		
		public void actionPerformed(ActionEvent e) {
			JComboBox cb = (JComboBox)e.getSource();
	        int index = cb.getSelectedIndex();
	        rebuildMaterialPanel(materialFiles[index]);	
		}
		
		class MaterialPanel extends JPanel implements MouseListener {
			
			private Material material;
			
			public MaterialPanel(Material material) {
				this.material = material;
				setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
				Border border = BorderFactory.createEmptyBorder(4, 4, 8, 4);
				//Border border = BorderFactory.createDashedBorder(Color.RED);
				setBorder(border);
				JPanel buttonPanel = new JPanel();
				buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
				JLabel moveLabel = new JLabel("Material");
				final JTextField nameField = new JTextField(material.name +  " (" + material.textureFileName + ")", 30);
				nameField.setEditable(false);
				buttonPanel.add(moveLabel);
				buttonPanel.add(nameField);
				add(buttonPanel);
				if (material.texture != null) {
					MaterialImagePanel materialPanel = new MaterialImagePanel(material);
					add(materialPanel);
				}
				addMouseListener(MaterialPanel.this);
				
			}
			
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void mouseReleased(MouseEvent arg0) {
				editor.applyMaterial(material);
				
			}

			class MaterialImagePanel extends JPanel {

				private Material material;
				private BufferedImage nativeImage;
				private Dimension dimension;

				//	float zoomFactorX;
				//	float zoomFactorY;

				public MaterialImagePanel(Material material) {
					this.material = material;

					//setFocusable(true);
					int w = material.texture.getWidth();
					int h = material.texture.getHeight();

					this.dimension = new Dimension(w, h);

					InputStream is = getClass().getResourceAsStream("/res/" + material.textureFileName);
					try {
						nativeImage = ImageIO.read(is);
					} catch (IOException e) {
						e.printStackTrace();
					}

					//setLayout(new FlowLayout(FlowLayout.CENTER));

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
					g.drawImage(nativeImage, (MaterialImagePanel.this.getWidth() - nativeImage.getWidth()) / 2, (MaterialImagePanel.this.getHeight() - nativeImage.getHeight()) / 2, null);
				}

			}

		}
		
		
	}
	
	
	
}



