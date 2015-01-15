package interfac.util;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;

import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * Cette classe permet de définir un nouveau layout permettant d'avoir juste un scroll vertical lors de l'ajout d'éléments.
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
			//  Chaque ligne doit s'adapter avec la largeur allouée par le conteneur.
			//  Quand le conteneur a une largeur de 0, la largeur préféré du conteneur
			//  n'a pas encore été calculés  alors on demande le maximum.
	
			int targetWidth = target.getSize().width;
	
			if (targetWidth == 0)
				targetWidth = Integer.MAX_VALUE;
	
			int hgap = getHgap();
			int vgap = getVgap();
			Insets insets = target.getInsets();
			int horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2);
			int maxWidth = targetWidth - horizontalInsetsAndGap;
	
			//  Adapter les composants dans la largeur allouée.
	
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
	
					// Ajout d'un écart pour tous les composants après le premier.
	
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
			//  on a besoin de vérifier que preferred size est plus petite que la taille du conteneur ciblé
			//  alors la modification de la taille du conteneur fonctionne correctement
			//  La suppression de l'écart est un moyen facile de le faire.
	
			Container scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane.class, target);
	
			if (scrollPane != null)
			{
				dim.width -= (hgap + 1);
			}
	
			return dim;
		}
	}

	/**
	 *  Une nouvelle ligne a été completée. On utilise les dimensions de celle-ci
	 *  pour mettre à jour la preferred size pour le conteneur.
	 *
	 *  @param dim met à jour la largeur et la hauteur quand c'est approprié.
	 *  @param rowWidth la largeur de la ligne à ajouter
	 *  @param rowHeight la hauteur de la ligne à ajouter
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
