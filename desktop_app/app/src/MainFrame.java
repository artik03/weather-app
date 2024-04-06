import javax.swing.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.awt.*;
import java.net.URL;
import java.util.Objects;
import com.github.weisj.jsvg.*;
import com.github.weisj.jsvg.geometry.size.FloatSize;
import com.github.weisj.jsvg.parser.*;

import net.coobird.thumbnailator.Thumbnails;

import java.io.IOException;
import java.io.InputStream;
import java.awt.FontFormatException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class MainFrame extends JFrame {
	static GettingWeatherData gwd = new GettingWeatherData();
	
	// global variables
	private static final int FRAME_WIDTH = 350;
    private static final int FRAME_HEIGHT = 500;
    private static Font juraFont;
    private static Color fontColor = new Color(0xefbcd5);
    public static String dKey = "";
    
 // global components
    private static GradientPanel contentPane = new GradientPanel();
    private static JPanel mainP = new JPanel();
 	private RoundedCornerTextField searchField;
 	private static JLabel locationLabel;
    private static JLabel tempLabel;
    private static JLabel feelsLikeLabel;
    private static JLabel humidityValue;
    private static JLabel visibilityValue;
    private static JLabel windSpeedValue;
    private static JLabel windDirValue;
    private static JLabel sunriseLabel;
    private static JLabel sunsetLabel;
    private static JLabel descriptionLabel;
    private static JLabel weatherIconLabel;
    private static JPanel modal = new JPanel();
    private static JLabel modalLabel;

 	
 	public static BufferedImage resizeBufImg(BufferedImage img, int newW, int newH) throws IOException {
 	    return Thumbnails.of(img).size(newW, newH).asBufferedImage();
 	}

    private static void loadSVG(String iconName) {
    	try {
    		SVGLoader loader = new SVGLoader();
            URL svgUrl = MainFrame.class.getResource("/images/"+iconName+".svg");
            SVGDocument document = loader.load(Objects.requireNonNull(svgUrl, "SVG file not found"));
            FloatSize size = document.size();
            BufferedImage weatherIcon = new BufferedImage((int) size.width,(int) size.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = weatherIcon.createGraphics();
            document.render(null,g);
            g.dispose();
            weatherIcon = resizeBufImg(weatherIcon, 100, 100);
            
            weatherIconLabel.setIcon(null);
            weatherIconLabel.setIcon(new ImageIcon(weatherIcon));
            weatherIconLabel.repaint(); 
            weatherIconLabel.revalidate();
            weatherIconLabel.update(weatherIconLabel.getGraphics());
            

        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }
	
	private static void registerFont() {
		try {
			InputStream fontStream = MainFrame.class.getResourceAsStream("/font/Jura-Regular.ttf");
			if (fontStream != null) {
				juraFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
			} else {
				throw new IOException("Font file not found.");
			}
		} catch (IOException | FontFormatException e) {
			juraFont = new Font("Arial", Font.PLAIN, 14);
			e.printStackTrace(); // Handle the error appropriately (e.g., logging)
		}
		
	}
    
    public static void showData(JSONObject weatherData) {
    	// Extract required data from the JSON object
    	JSONObject main = (JSONObject) weatherData.get("main");
    	JSONObject sys = (JSONObject) weatherData.get("sys");
    	JSONObject weather = (JSONObject) (((JSONArray) weatherData.get("weather")).get(0));
    	JSONObject wind = (JSONObject) weatherData.get("wind");
    	
        String location = (String) weatherData.get("name") + " " + sys.get("country");
        String temperature = Double.toString((double) main.get("temp"));
        String feelsLike = Double.toString((double) main.get("feels_like"));
        String humidity = Long.toString((long) main.get("humidity"));
        long visibilityLong = (long)weatherData.get("visibility");
        String windSpeed = Double.toString((double)wind.get("speed"));
        long windDirectionLong = (long)wind.get("deg");
        long sunriseLong = (long) sys.get("sunrise");
        long sunsetLong = (long) sys.get("sunset");
        String description = (String) weather.get("description");
        String icon = (String) weather.get("icon");
        
        // Change data to correct format
        String windDirection = gwd.windDegToDir(windDirectionLong);
        String visibility = gwd.getVisibilityDescription(visibilityLong);
        String sunrise = gwd.timestampToTime(sunriseLong);
        String sunset = gwd.timestampToTime(sunsetLong);
        

        // Set the data to the UI components
        locationLabel.setText(location);
        tempLabel.setText(temperature + "°C");
        feelsLikeLabel.setText("Feels like " + feelsLike + "°C");
        humidityValue.setText(humidity + "%");
        visibilityValue.setText(visibility);
        windSpeedValue.setText(windSpeed + " km/h");
        windDirValue.setText(windDirection);
        sunriseLabel.setText("Sunrise: " + sunrise);
        sunsetLabel.setText("Sunset: " + sunset);
        descriptionLabel.setText(
        		String.format("<html><body style='text-align: center;'>%s</body></html>", description)
        );
        loadSVG(icon);
        
        showMain();
    }
	
    
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		 
		
        if (args.length < 1) {
            System.err.println("No key found.");
            System.exit(1); // Exit with error code
        }
        dKey = args[0];
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// font
					registerFont();
					
					// set window icon
					MainFrame frame = new MainFrame();
					ImageIcon logo = null;

				      java.net.URL imgURL = MainFrame.class.getResource("/images/logo.png");
				      if (imgURL != null) {
				    	 logo = new ImageIcon(imgURL);
				         frame.setIconImage(logo.getImage());
				      } else {
				    	  Image transparentIcon = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE);
				    	  frame.setIconImage(transparentIcon);

				      }
					
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * helper
	 */
	private static void showMain() {
		contentPane.remove(modal);
		contentPane.add(mainP);
		
		contentPane.repaint(); 
		contentPane.revalidate();
	}
	private static void showModal(String type) {
		
		if (type.equals("loading")) {
			modalLabel.setText("Loading...");
		} else if (type.equals("error")) {
			modalLabel.setText(
				String.format("<html><body style='text-align: center;'>%s</body></html>", "We couldn't find weather information for the city you searched :( Check for internet onnection and spelling mistakes."));
		}
		
		contentPane.remove(mainP);
		contentPane.add(modal);
		
		contentPane.repaint(); 
		contentPane.revalidate();
	}
	
	private static JLabel customLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(juraFont.deriveFont(Font.PLAIN, 16));
        label.setForeground(fontColor);
        
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }
	
	private static JPanel customPanel(int w, int h) {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setPreferredSize(new Dimension(w, h));
		return panel;
	}
	

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setResizable(false);
		setTitle("Weather Frog");
		setBackground(new Color(255, 255, 255));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100 , 100, FRAME_WIDTH, FRAME_HEIGHT);
		
		/*
		 * content pane
		 */
		
		contentPane.setBorder(null);
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel header = new JPanel();
		header.setOpaque(false);
		header.setBounds(0, 0, 336, 70);
		contentPane.add(header);
		header.setLayout(null);
		
			searchField = new RoundedCornerTextField();
			searchField.setText("Enter city name here...");
			searchField.setBounds(12, 15, 220, 40);
			searchField.setColumns(10);
			searchField.setBackground(new Color(0x2e294e)); 
			searchField.setForeground(fontColor);
			searchField.setFont(juraFont.deriveFont(Font.PLAIN, 16));
			header.add(searchField);
			
			RoundedCornerBtn searchBtn = new RoundedCornerBtn("Search");
			searchBtn.setBounds(239, 15, 85, 40);
			searchBtn.setBackground(new Color(0x2e294e)); 
			searchBtn.setFont(juraFont.deriveFont(Font.PLAIN, 16));
			searchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			searchBtn.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	String text = searchField.getText();
	            	searchField.setText("");
	            	
	            	showModal("loading");
	            	SwingUtilities.invokeLater(() -> {
	            		String key = Encryption.getKey();
	            		JSONObject weatherData = gwd.fetchWeatherData(text, key, dKey);
	            		if (weatherData.size() == 0) {
	            			showModal("error");
	            		} else {
	            			showData(weatherData);
	            		}
	            	});
	            }
	        });
			header.add(searchBtn);
			
		
		modal.setOpaque(false);
		modal.setBounds(0, 0, 336, 393);
		contentPane.add(modal);
		modal.setLayout(new BorderLayout(0, 0));
		
			modalLabel = customLabel("Search for city to find out its weather!");
			modalLabel.setHorizontalAlignment(SwingConstants.CENTER);
			modalLabel.setPreferredSize(new Dimension(168, 393));
			modal.add(modalLabel, BorderLayout.CENTER);
		
		mainP.setOpaque(false);
		mainP.setBounds(0, 70, 336, 393);
		//contentPane.add(mainP);
		mainP.setLayout(new BorderLayout(0, 0));
			
			
		    JPanel panelUp = customPanel(336, 146);
		    mainP.add(panelUp, BorderLayout.NORTH);
			panelUp.setLayout(new BorderLayout(0, 0));
			
				JPanel panelUpL = customPanel(168, 146);
				panelUp.add(panelUpL, BorderLayout.WEST);
				panelUpL.setLayout(new BorderLayout(0, 0));
				
					locationLabel = customLabel("");
					panelUpL.add(locationLabel, BorderLayout.NORTH);
					
					tempLabel = customLabel("");
					tempLabel.setFont(juraFont.deriveFont(Font.PLAIN, 36));
					panelUpL.add(tempLabel, BorderLayout.CENTER);
					
					feelsLikeLabel = customLabel("");
					feelsLikeLabel.setVerticalAlignment(SwingConstants.TOP);
					panelUpL.add(feelsLikeLabel, BorderLayout.SOUTH);
				
				JPanel panelUpR = customPanel(168, 168);
				panelUp.add(panelUpR, BorderLayout.EAST);
				panelUpR.setLayout(new BorderLayout(0, 0));
					
					weatherIconLabel = new JLabel("");
					weatherIconLabel.setHorizontalAlignment(SwingConstants.CENTER);
					panelUpR.add(weatherIconLabel, BorderLayout.CENTER);
					
					descriptionLabel = customLabel("");
					descriptionLabel.setVerticalAlignment(SwingConstants.TOP);
					panelUpR.add(descriptionLabel, BorderLayout.SOUTH);
					
			
			
			JPanel panelCt = customPanel(336, 146);
			mainP.add(panelCt, BorderLayout.CENTER);
			panelCt.setLayout(new GridLayout(4, 2, 0, 0));
			
			JLabel humidityLabel = customLabel("Humidity:");
			humidityLabel.setVerticalAlignment(SwingConstants.BOTTOM);
			panelCt.add(humidityLabel);

			humidityValue = customLabel("");
			humidityValue.setVerticalAlignment(SwingConstants.BOTTOM);
			panelCt.add(humidityValue);

			JLabel visibilityLabel = customLabel("Visibility:");
			visibilityLabel.setVerticalAlignment(SwingConstants.BOTTOM);
			panelCt.add(visibilityLabel);

			visibilityValue = customLabel("");
			visibilityValue.setVerticalAlignment(SwingConstants.BOTTOM);
			panelCt.add(visibilityValue);

			JLabel windSpeedLabel = customLabel("Wind speed:");
			windSpeedLabel.setVerticalAlignment(SwingConstants.BOTTOM);
			panelCt.add(windSpeedLabel);

			windSpeedValue = customLabel("");
			windSpeedValue.setVerticalAlignment(SwingConstants.BOTTOM);
			panelCt.add(windSpeedValue);

			JLabel windDirLabel = customLabel("Wind direction:");
			windDirLabel.setVerticalAlignment(SwingConstants.BOTTOM);
			panelCt.add(windDirLabel);

			windDirValue = customLabel("");
			windDirValue.setVerticalAlignment(SwingConstants.BOTTOM);
			panelCt.add(windDirValue);
				
			
		    JPanel panelDw = customPanel(336, 101);
		    mainP.add(panelDw, BorderLayout.SOUTH);
			panelDw.setLayout(new BorderLayout(0, 0));
			
				JPanel panelDwL = customPanel(168, 101);
				panelDw.add(panelDwL, BorderLayout.WEST);
				panelDwL.setLayout(new BorderLayout(0, 0));
				
				sunriseLabel = customLabel("");
				panelDwL.add(sunriseLabel, BorderLayout.CENTER);
				
				JPanel panelDwR = customPanel(168, 101);
				panelDw.add(panelDwR, BorderLayout.EAST);
				panelDwR.setLayout(new BorderLayout(0, 0));
				
				sunsetLabel = customLabel("");
				panelDwR.add(sunsetLabel);
		
	}
}