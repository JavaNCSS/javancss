/*
Copyright (C) 2014 Chr. Clemens Lee <clemens@kclee.com>.

This file is part of JavaNCSS
(http://javancss.codehaus.org/).

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA*/

package javancss;

import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.util.*;
import java.text.*;
import java.io.*;
import java.util.List;

import javax.swing.*;

/**
 * Main class used to start JavaNCSS in GUI mode from other
 * java applications. To start JavaNCSS from the command line,
 * gui mode or not, class 'Main' is used.
 *
 * @author  <a href="http://www.kclee.de/clemens/">Chr. Clemens Lee</a> (<a href="mailto:clemens@kclee.com"><i>clemens@kclee.com</i></a>)
 * @version $Id$
 */
public class JavancssFrame extends JFrame {
    public static final String S_PACKAGES = "Packages";
    public static final String S_CLASSES = "Classes";
    public static final String S_METHODS = "Methods";

    private JTextArea _txtPackage;
    private JTextArea _txtObject;
    private JTextArea _txtFunction;
    private JTextArea _txtError;

    private JTabbedPane _pTabbedPane = null;

    private Font pFont = new Font("Monospaced", Font.PLAIN, 12);

    private boolean _bNoError = true;

    public void save() {
        File targetDirectory = new File(".");
        File packagesFile = new File( targetDirectory, "javancss-packages.txt" );
        File classesFile  = new File( targetDirectory, "javancss-classes.txt" );
        File methodsFile  = new File( targetDirectory, "javancss-methods.txt" );

        String sSuccessMessage = "Data appended successfully to the following files:";

        try {
            appendFile( packagesFile, _txtPackage.getText() );
            sSuccessMessage += "\n" + packagesFile;
        } catch(Exception e) {
            JOptionPane.showMessageDialog( this, "Could not append to file '" + classesFile + "'.\n" + e, "Error", JOptionPane.ERROR_MESSAGE );
        }

        try {
            appendFile( classesFile, _txtObject.getText() );
            sSuccessMessage += "\n" + classesFile;
        } catch(Exception e) {
            JOptionPane.showMessageDialog( this, "Could not append to file '" + classesFile + "'.\n" + e, "Error", JOptionPane.ERROR_MESSAGE );
        }

        try {
            appendFile( methodsFile, _txtFunction.getText() );
            sSuccessMessage += "\n" + methodsFile;
        } catch(Exception e) {
            JOptionPane.showMessageDialog( this, "Could not append to file '" + methodsFile + "'.\n" + e, "Error", JOptionPane.ERROR_MESSAGE );
        }

        JOptionPane.showMessageDialog( this, sSuccessMessage, "Message", JOptionPane.INFORMATION_MESSAGE );
    }

    public static void appendFile( File file, String content ) throws IOException
    {
        FileWriter writer = new FileWriter( file, true );
        writer.write( content );
        writer.close();
    }

    public JavancssFrame( List<String> files )
    {
        super( "JavaNCSS: " + files );

        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        setIconImage( new ImageIcon( getClass().getClassLoader().getResource( "javancss/javancssframe.gif" ) ).getImage() );

        createMenuBar();

        GridBagLayout layout = new GridBagLayout();

        getContentPane().setLayout(layout);
        
        JPanel busyPanel = new JPanel();
        busyPanel.setLayout( new BorderLayout() );
        busyPanel.add( new JLabel( new ImageIcon( getClass().getResource("/javancss/busy-squares.gif") ) ) );
        
        getContentPane().add(busyPanel);

        pack();
        setSize(800, 600);
        
        // center the frame on the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation( ( screenSize.width - getWidth() ) / 2, ( screenSize.height - getHeight() ) / 2 );
    }

    private void createMenuBar()
    {
        JMenuBar menubar = new JMenuBar();
        menubar.add( createFileMenu() );
        menubar.add( createHelpMenu() );

        setJMenuBar( menubar );
    }

    private JMenu createFileMenu()
    {
        JMenu menu = new JMenu( "File" );

        JMenuItem item = new JMenuItem( "Save" );
        item.setAccelerator( KeyStroke.getKeyStroke( 'S', InputEvent.CTRL_MASK ) );
        item.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent event )
            {
                save();
            }
        } );
        menu.add( item );

        item = new JMenuItem( "Exit" );
        item.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent event )
            {
                dispose();
            }
        } );

        menu.add( item );

        return menu;
    }

    private JMenu createHelpMenu()
    {
        JMenu menu = new JMenu( "Help" );

        JMenuItem item = new JMenuItem( "Project page...", 'P' );
        item.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent event )
            {
                try
                {
                    Desktop.getDesktop().browse( new URI( "https://github.com/JavaNCSS/javancss" ) );
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                }
            }
        } );

        menu.add( item );
        menu.addSeparator();
        
        item = new JMenuItem( "About..." );
        item.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                AboutDialog about = new AboutDialog( JavancssFrame.this, "JavaNCSS", getClass().getPackage().getSpecificationVersion(), "Chr. Clemens Lee & co" );
                about.dispose();
            }
        } );
        
        menu.add( item );
        
        return menu;
    }

    public void showJavancss(Javancss pJavancss_) throws IOException {
        getContentPane().removeAll();
        getContentPane().setLayout(new BorderLayout());
        _bNoError = true;
        if (pJavancss_.getLastErrorMessage() != null && pJavancss_.getNcss() <= 0) {
            _bNoError = false;
            JTextArea txtError = new JTextArea();
            String sError = "Error in Javancss: " +
                   pJavancss_.getLastErrorMessage();
            txtError.setText(sError);
            JScrollPane jspError = new JScrollPane(txtError);
            getContentPane().add(jspError, BorderLayout.CENTER);
        } else {
            JPanel pPanel = new JPanel(true);
            pPanel.setLayout(new BorderLayout());
            _pTabbedPane = new JTabbedPane();
            _pTabbedPane.setDoubleBuffered(true);

            _txtPackage = new JTextArea();
            _txtPackage.setFont(pFont);
            JScrollPane jspPackage = new JScrollPane(_txtPackage);
            int inset = 5;
            jspPackage.setBorder( BorderFactory.
                                  createEmptyBorder
                                  ( inset, inset, inset, inset ) );
            _pTabbedPane.addTab("Packages", null, jspPackage);

            _txtObject = new JTextArea();
            _txtObject.setFont(pFont);
            JScrollPane jspObject = new JScrollPane(_txtObject);
            jspObject.setBorder( BorderFactory.
                                  createEmptyBorder
                                  ( inset, inset, inset, inset ) );
            _pTabbedPane.addTab("Classes", null, jspObject);

            _txtFunction = new JTextArea();
            _txtFunction.setFont(pFont);
            JScrollPane jspFunction = new JScrollPane(_txtFunction);
            jspFunction.setBorder( BorderFactory.
                                  createEmptyBorder
                                  ( inset, inset, inset, inset ) );
            _pTabbedPane.addTab("Methods", null, jspFunction);

            // date and time
            String sTimeZoneID = System.getProperty("user.timezone");
            if (sTimeZoneID.equals("CET")) {
                sTimeZoneID = "ECT";
            }
            TimeZone pTimeZone = TimeZone.getTimeZone(sTimeZoneID);

            SimpleDateFormat pSimpleDateFormat
                   = new SimpleDateFormat("EEE, MMM dd, yyyy  HH:mm:ss");//"yyyy.mm.dd e 'at' hh:mm:ss a z");
            pSimpleDateFormat.setTimeZone(pTimeZone);
            String sDate = pSimpleDateFormat.format(new Date()) + " " + pTimeZone.getID();

            StringWriter sw=new StringWriter();
            pJavancss_.printPackageNcss(sw);

            _txtPackage.setText(sDate + "\n\n" + sw.toString());
            
            sw=new StringWriter();
            pJavancss_.printObjectNcss(sw);
            
            _txtObject.setText(sDate + "\n\n" + sw.toString());

            sw=new StringWriter();
            pJavancss_.printFunctionNcss(sw);
            _txtFunction.setText(sDate + "\n\n" + sw.toString());

            if (pJavancss_.getLastErrorMessage() != null) {
                _txtError = new JTextArea();
                String sError = "Errors in Javancss:\n\n" +
                       pJavancss_.getLastErrorMessage();
                _txtError.setText(sError);
                JScrollPane jspError = new JScrollPane(_txtError);
                jspError.setBorder( BorderFactory.
                                  createEmptyBorder
                                  ( inset, inset, inset, inset ) );
                getContentPane().add(jspError, BorderLayout.CENTER);
                _pTabbedPane.addTab("Errors", null, jspError);
            }

            pPanel.add(_pTabbedPane, BorderLayout.CENTER);
            getContentPane().add(pPanel, BorderLayout.CENTER);
        }

        validate();
        repaint();
    }

    public void setSelectedTab(String sTab_) {
        if (sTab_ == null || sTab_.trim().length() == 0) {
            throw new IllegalArgumentException();
        }

        if (!_bNoError) {
            return;
        }
        if (sTab_.equals(S_METHODS)) {
            /*_pTabbedPane.setSelectedComponent(_txtFunction);*/
            _pTabbedPane.setSelectedIndex(2);
        } else if (sTab_.equals(S_CLASSES)) {
            /*_pTabbedPane.setSelectedComponent(_txtObject);*/
            _pTabbedPane.setSelectedIndex(1);
        } else {
            /*_pTabbedPane.setSelectedComponent(_txtPackage);*/
            _pTabbedPane.setSelectedIndex(0);
        }
    }

    private static class AboutDialog extends JDialog
    {
        private JLabel labelName = new JLabel();
        private JLabel labelVersion = new JLabel();
        private JLabel labelAuthor = new JLabel( "Author:" );
        private JLabel labelRealAuthor = new JLabel();
        private JLabel labelDate = new JLabel( "Last change:" );
        private JLabel labelRealDate = new JLabel();

        public AboutDialog( Frame parent, String programName, String version, String author)
        {
            super( parent, "About", true );

            labelName.setText( programName );
            labelName.setFont( labelName.getFont().deriveFont( Font.BOLD, 14 ) );
            
            labelVersion.setText( version );
            labelVersion.setFont( labelVersion.getFont().deriveFont( Font.PLAIN ) );
            labelRealDate.setText( getDate() );
            labelRealDate.setFont( labelRealDate.getFont().deriveFont( Font.PLAIN ) );
            labelRealAuthor.setText( author );
            labelRealAuthor.setFont( labelRealAuthor.getFont().deriveFont( Font.PLAIN ) );

            ( ( JPanel ) getContentPane() ).setBorder( BorderFactory.createEmptyBorder( 25, 40, 25, 40 ) );

            getContentPane().setLayout( new GridLayout( 3, 2, 0, 10 ) );

            getContentPane().add( labelName );
            getContentPane().add( labelVersion );
            getContentPane().add( labelAuthor );
            getContentPane().add( labelRealAuthor );
            getContentPane().add( labelDate );
            getContentPane().add( labelRealDate );

            pack();
            
            // center the dialog relatively to the parent frame
            Dimension parentSize = parent.getSize();
            setLocation(parent.getLocation().x + ( parentSize.width - getWidth() ) / 2,
                        parent.getLocation().y + ( parentSize.height - getHeight() ) / 2 );
            
            setVisible( true );
        }

        private String getDate() {
            try
            {
                String classfile = "/" + getClass().getName().replaceAll( "\\.", "/" ) + ".class";
                long timestamp = getClass().getResource( classfile ).openConnection().getLastModified();
                return DateFormat.getDateTimeInstance().format( new Date(timestamp) );
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
            
            return null;
        }
    }
}
