package interfac.util;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;

import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * Cette classe permet de d�finir un nouveau layout permettant d'avoir juste un scroll vertical lors de l'ajout d'�l�ments.
 */
@SuppressWarnings("serial")
public class ScrollVerticalLayout extends FlowLayout
{
	@SuppressWarnings("unused")
	private Dimension preferredLayoutSize;
	//constructeur
	public ScrollVerticalLayout()
	{
		super();
	}
	@Override
	public Dimension preferredLayoutSize(Container target)
	{
		return layoutSize(target, true);
	}

	@Override
	public Dimension minimumLayoutSize(Container target)
	{
		Dimension minimum = layoutSize(target, false);
		minimum.width -= (getHgap() + 1);
		return minimum;
	}
	
	private Dimension layoutSize(Container target, boolean preferred)
	{
		synchronized (target.getTreeLock())
		{
			//  Chaque ligne doit s'adapter avec la largeur allou�e par le conteneur.
			//  Quand le conteneur a une largeur de 0, la largeur pr�f�r� du conteneur
			//  n'a pas encore �t� calcul�s  alors on demande le maximum.
	
			int targetWidth = target.getSize().width;
	
			if (targetWidth == 0)
				targetWidth = Integer.MAX_VALUE;
	
			int hgap = getHgap();
			int vgap = getVgap();
			Insets insets = target.getInsets();
			int horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2);
			int maxWidth = targetWidth - horizontalInsetsAndGap;
	
			//  Adapter les composants dans la largeur allou�e.
	
			Dimension dim = new Dimension(0, 0);
			int rowWidth = 0;
			int rowHeight = 0;
	
			int nmembers = target.getComponentCount();
	
			for (int i = 0; i < nmembers; i++)
			{
				Component m = target.getComponent(i);
	
				if (m.isVisible())
				{
					Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
	
					// Si on peut pas ajouter le composant dans la ligne courante,
					// on commence une nouvelle ligne.
	
					if (rowWidth + d.width > maxWidth)
					{
						addRow(dim, rowWidth, rowHeight);
						rowWidth = 0;
						rowHeight = 0;
					}
	
					// Ajout d'un �cart pour tous les composants apr�s le premier.
	
					if (rowWidth != 0)
					{
						rowWidth += hgap;
					}
	
					rowWidth += d.width;
					rowHeight = Math.max(rowHeight, d.height);
				}
			}
	
			addRow(dim, rowWidth, rowHeight);
	
			dim.width += horizontalInsetsAndGap;
			dim.height += insets.top + insets.bottom + vgap * 2;
	
			//	Quand on utilise un scrollpane ou un DecoratedLookAndFeel
			//  on a besoin de v�rifier que preferred size est plus petite que la taille du conteneur cibl�
			//  alors la modification de la taille du conteneur fonctionne correctement
			//  La suppression de l'�cart est un moyen facile de le faire.
	
			Container scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane.class, target);
	
			if (scrollPane != null)
			{
				dim.width -= (hgap + 1);
			}
	
			return dim;
		}
	}

	/**
	 *  Une nouvelle ligne a �t� complet�e. On utilise les dimensions de celle-ci
	 *  pour mettre � jour la preferred size pour le conteneur.
	 *
	 *  @param dim met � jour la largeur et la hauteur quand c'est appropri�.
	 *  @param rowWidth la largeur de la ligne � ajouter
	 *  @param rowHeight la hauteur de la ligne � ajouter
	 */
	private void addRow(Dimension dim, int rowWidth, int rowHeight)
	{
		dim.width = Math.max(dim.width, rowWidth);

		if (dim.height > 0)
		{
			dim.height += getVgap();
		}

		dim.height += rowHeight;
	}
}
