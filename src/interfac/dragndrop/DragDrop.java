package interfac.dragndrop;



import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.Map.Entry;

import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;


/**
 * 
 * Cette classe gère les actions drag&drop sur des composants.
 * 
 * Les éléments draggables sont déclarés à l'aide de la méthode statique 
 * ajouterDragable, en précisant un nom pour le type d'action.
 * 
 * Les composants récepteurs du drop précisent à l'aide de ajouterRecepteur
 * quel type d'action ils supportent (dans un String[]) et un MouseAdapter :
 * 
 * <p>mousePressed : début d'un drag&drop compatible avec le composant</p>
 * <p>mouseEntered : entrée du drag sur le composant</p>
 * <p>mouseExited : sortie du drag sur le composant</p>
 * <p>mouseReleased : réception du drop par le composant</p>
 * <p>mouseClicked : fin d'un drag&drop compatible avec le composant </p>
 * 
 * @author Mathieu Guinin
 *
 */

public class DragDrop {

	static private Hashtable<Component,MouseAdapter> lienComposantAction;
	static private Component focus; 
	static private Component focusFin; 
	static private MouseAdapter actionFocus;
	static private JLayeredPane panPointeur ;
	static private Object contenuDrag;
	static private Component sourceDrag;
	static private Hashtable<String
					,Hashtable<Component
								,MouseAdapter>> 
			recepteurs = new Hashtable<String,Hashtable<Component,MouseAdapter>> (10);
	
	static private Hashtable<Component,PropDrag> lienCompProp;
	
	/*
	 * 		Listener qui interprète les actions et les envoie au listener 
	 * 		Personnalise du composant
	 */
	
	private DragDrop(){};
	
	static private class PropDrag {
		private int indexDepart;
		private Point positionDepart;
		private Container parentDepart;
		private Point pointFin;
		private Point p0;
	}
	
	static private class ListenerDrag extends MouseAdapter {
		
		private Collection<? extends Component> listeDrag;
		private Collection<? extends Component> listeDragDefaut;
		final public Object contenuTransfert;
		final public String nomAction;
		
		
		public ListenerDrag(String nomAction,Collection<Component> listeDrag,Object contenuTransfert) {
			this.listeDrag = listeDrag;
			listeDragDefaut = listeDrag;
			this.nomAction = nomAction;
			this.contenuTransfert =contenuTransfert;
		}
		
		static Cursor moveCursor = new Cursor(Cursor.MOVE_CURSOR);
		static Cursor defautCursor = new Cursor(Cursor.DEFAULT_CURSOR);
		
		private MouseAdapter startDrag = new MouseAdapter(){
			@Override
			public void mouseDragged(MouseEvent e) {
				e.getComponent().addMouseMotionListener(ListenerDrag.this);
				e.getComponent().removeMouseMotionListener(this);
				
				contenuDrag = contenuTransfert;
				lienComposantAction = recepteurs.get(nomAction);
				sourceDrag = e.getComponent();
				if (lienComposantAction != null)
					for (Entry<Component, MouseAdapter> couple : lienComposantAction.entrySet()) {
						couple.getValue().mousePressed(e);
					}
				if (listeDrag != null && !listeDrag.isEmpty()) {
					panPointeur = JLayeredPane.getLayeredPaneAbove(e.getComponent());
					Point m = panPointeur.getMousePosition();
					
					DragDrop.lienCompProp  = new Hashtable<Component,PropDrag>(listeDrag.size());
					for (Component comp : listeDrag) {
						PropDrag prop = new PropDrag();
						prop.parentDepart = comp.getParent();
						if (prop.parentDepart != null) {
							prop.indexDepart = prop.parentDepart.getComponentZOrder(comp);
							prop.positionDepart = comp.getLocation();
							Point location = comp.getLocationOnScreen();
							prop.p0 = new Point(e.getLocationOnScreen().x-location.x,e.getLocationOnScreen().y-location.y);
						} else prop.p0 = e.getPoint();
						lienCompProp.put(comp, prop);
						panPointeur.add(comp);
						panPointeur.setLayer(comp,JLayeredPane.DRAG_LAYER );
						comp.setCursor(moveCursor);
						
						if (m!=null) comp.setLocation(m.x-prop.p0.x,m.y-prop.p0.y);
					}
				}
			}
		};
		
		
		@Override
		public void mousePressed(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				e.getComponent().addMouseMotionListener(startDrag);
			}
		}
		
		
		@Override
		public void mouseDragged(MouseEvent e) {
			Point m = panPointeur.getMousePosition();
			if (m!=null) {
				for (Component comp : listeDrag) {
					PropDrag prop = lienCompProp.get(comp);
					comp.setLocation(m.x-prop.p0.x,m.y-prop.p0.y);
					panPointeur.remove(comp);
				}
				Component cible = panPointeur.findComponentAt(m.x,m.y);
				if (cible != focus) {
					if (actionFocus != null) 
						actionFocus.mouseExited(new MouseEvent(focus,MouseEvent.MOUSE_EXITED,e.getWhen(),e.getModifiers(),e.getX(),e.getY(),e.getXOnScreen(),e.getYOnScreen(),e.getClickCount(),e.isPopupTrigger(),e.getButton()));
					do  { 
						actionFocus = lienComposantAction.get(cible);
						focus = cible;
						cible = cible.getParent();
					} while (cible != null && actionFocus == null);
					if (actionFocus != null) {
						actionFocus.mouseEntered(new MouseEvent(focus,MouseEvent.MOUSE_ENTERED,e.getWhen(),e.getModifiers(),e.getX(),e.getY(),e.getXOnScreen(),e.getYOnScreen(),e.getClickCount(),e.isPopupTrigger(),e.getButton()));
					}
					focusFin = focus;
				}
				for (Component comp : listeDrag) panPointeur.add(comp);
			}
		}
		
		
		@Override
		public void mouseReleased(MouseEvent e) {

			if (SwingUtilities.isLeftMouseButton(e)) {
				e.getComponent().removeMouseMotionListener(this);
				e.getComponent().removeMouseMotionListener(startDrag);
				
				for (Component comp : listeDrag) {
					PropDrag prop = lienCompProp.get(comp);
					if (prop != null) {
						Rectangle repaintRect = comp.getBounds();
						if (prop.parentDepart != null) {
							prop.pointFin = comp.getLocation();
							SwingUtilities.convertPointToScreen(prop.pointFin, panPointeur);
							if (prop.parentDepart.getLayout()==null) comp.setLocation(prop.positionDepart);
							prop.parentDepart.add(comp,prop.indexDepart);
						} else panPointeur.remove(comp);
						panPointeur.repaint(repaintRect);
						comp.setCursor(defautCursor);
						prop.parentDepart = null;
					}
				}
				
				if (actionFocus != null ) {
					actionFocus.mouseReleased(e);
					actionFocus.mouseExited(new MouseEvent(focus,e.getID(),e.getWhen(),e.getModifiers(),e.getX(),e.getY(),e.getXOnScreen(),e.getYOnScreen(),e.getClickCount(),e.isPopupTrigger(),e.getButton()));	
				}
				focus = null;
				actionFocus = null;
				
				
				if (lienComposantAction != null)
					for (Entry<Component, MouseAdapter> couple : lienComposantAction.entrySet()) {
						couple.getValue().mouseClicked(e);
					}
			}
		}
	};
	/*
	 *		Méthodes permettant d'ajouter des listeners de drap&drop aux composants
	 */
	
	static public void ajouterRecepteur(Component composent,MouseAdapter actions,String[] nomActions) {
		Hashtable<Component,MouseAdapter> liste;
		if (nomActions == null || nomActions.length == 0) nomActions = new String[]{""};
		for (String s : nomActions){
			if (!recepteurs.containsKey(s)) recepteurs.put(s, liste = new Hashtable<Component,MouseAdapter>(100));
			else liste = recepteurs.get(s);
			liste.put(composent,actions);
		}
	}
	
	
	
	static public Point positionFin(Component draggable) {
		PropDrag prop = lienCompProp.get(draggable);
		if (prop != null) {
			return prop.pointFin;
		} else return null;
	}
	
	static public Component focusFin() {
		return focusFin;
	}
	
	static public void ajouterDragable(Component draggable,String nomAction,Component iconDrag,Object contenuTransert)  {
		ArrayList<Component> liste ;
		if (iconDrag == null) liste = null;
		else {
			liste = new ArrayList<Component>();
			liste.add(iconDrag);
		}
		ListenerDrag listener = new ListenerDrag(nomAction,liste,contenuTransert);
		draggable.addMouseListener(listener);
	}
	
	static public void ajouterDragMultiple(Component draggable,String nomAction,Component iconDragDefaut,Collection<Component> iconDrags,Object contenuTransert)  {
		ListenerDrag listener = new ListenerDrag(nomAction,iconDrags,contenuTransert);
		draggable.addMouseListener(listener);
	}
	
	static public void changerObjetsDrag(Component draggable,Collection<? extends Component> objetsDrag) {
		ListenerDrag listen = getListeDrag(draggable);
		if (listen != null) {
			listen.listeDrag = objetsDrag;
		}
	}
	
	static public void changerDragDefaut(Component draggable) {
		ListenerDrag listen = getListeDrag(draggable);
		if (listen != null) {
			listen.listeDrag = listen.listeDragDefaut;
		}
	}
	static private ListenerDrag getListeDrag(Component draggable) {
		for (MouseListener listen : draggable.getMouseListeners()) {
			if (listen instanceof ListenerDrag) {
				return (ListenerDrag) listen;
			}
		}
		return null;
	}
	
	
	static public void retirerDragable(Component draggable,final String nomAction)  {
		for (MouseListener m : draggable.getMouseListeners()) {
			if ( m instanceof ListenerDrag) {
				ListenerDrag ldrag = ((ListenerDrag) m);
				if (ldrag.nomAction == nomAction || nomAction == null) {
					draggable.removeMouseListener(ldrag);
				}
			}
		}
	}
	
	static public void retirerRecepteur(Component composent,String[] nomActions) {
		if (nomActions == null || nomActions.length == 0) nomActions = new String[]{""};
		for (String s : nomActions){
			if (recepteurs.contains(s)) {
				recepteurs.get(s).remove(composent);
			}
		}
	}
	
	static public Object getContenuDrag() {
		return contenuDrag;
	}
	
	static public Component getSourceDrag() {
		return sourceDrag;
	}
}

