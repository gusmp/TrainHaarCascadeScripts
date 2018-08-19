package es.kcsolutions;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.Map;

import org.opencv.core.Mat;
import org.yaml.snakeyaml.Yaml;

import es.kcsolutions.opencv.OpenCvWrapper;
import boofcv.io.UtilIO;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.GrayF32;

/**
 * Esta aplicación parsea los archivos yaml creados con la utilidad PlateTagger (OpenAnpr) y recorta las matriculas etiquetadas.
 * Tiene caracteristicas adicionales como:
 * - añadir padding (variables padding_*)
 * - indicar si la matricula es larga o cuadrada (indicar en la variable 'quad' el texto a oner en el nombre del archivo si es cuadrada)
 * - sufijar las matruculas recortadas (variable 'suffix') para evitar si la imagen es completa o solo la matricula
 *
 */
public class Recortes {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		//String sourceFolder = "d:/maquinas_virtuales/sharedFolder/matriculas_carmen/03_pack03/";
		//String targetFolder = "d:/maquinas_virtuales/sharedFolder/matriculas_carmen/03_pack03/matriculas_recortadas";
		
		String sourceFolder = "d:/maquinas_virtuales/sharedFolder/matriculas_carmen/04_pack04_cuadradas/extra_no_borrar_yaml/";
		String targetFolder = "d:/maquinas_virtuales/sharedFolder/matriculas_carmen/04_pack04_cuadradas/matriculas_recortadas/";
		
		
		if (sourceFolder.endsWith("/") == false) sourceFolder += "/";
		if (targetFolder.endsWith("/") == false) targetFolder += "/";
		
		String suffix = "_r";
		
		int padding_left = 0;
		int padding_right = 0;
		int padding_top = 0;
		int padding_bottom = 0;
		
		String quad = "_q";
		int counter = 0;
		
		Yaml yaml = new Yaml();
		
		File[] sourceFolderList = new File(sourceFolder).listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File arg0, String arg1) {
				return arg1.contains(".yaml");
			}
		});
		
		System.out.println("Esta aplicación parsea los archivos yaml creados con la utilidad PlateTagger (OpenAnpr) y recorta las matriculas ");
		System.out.println("etiquetadas. Tiene caracteristicas adicionales como:");
		System.out.println("- añadir padding (variables padding_*)");
		System.out.println("- indicar si la matricula es larga o cuadrada (indicar en la variable 'quad' el texto a oner en el nombre del archivo si es cuadrada)");
		System.out.println("- sufijar las matruculas recortadas (variable 'suffix') para evitar si la imagen es completa o solo la matricula\n");
		
		System.out.println("Directorio origen:" + sourceFolder);
		System.out.println("Directorio destino:" + targetFolder);

		OpenCvWrapper ocvw = new OpenCvWrapper();
		
		try {
			
			for(File f : sourceFolderList) {
				System.out.println("Procesando..." + f.getName());
				
				Map<String, Object> values = (Map<String, Object>) yaml.load(new FileInputStream(f));
				
				//BufferedImage image = null;
				//GrayF32 imageBoofcv = null;
				
				Mat image = null;
				try {
					//image = UtilImageIO.loadImage(UtilIO.pathExample(sourceFolder + values.get("image_file")));
					//imageBoofcv = ConvertBufferedImage.convertFromSingle(image, null, GrayF32.class);
					
					image = ocvw.leerImagen(sourceFolder + values.get("image_file"));
					
					
				} catch(Exception exc) {
					System.out.println("Error leyendo imgen: " +  sourceFolder + values.get("image_file"));
					System.out.println("Error: " + exc.toString());
					continue;
				}

				String[] points = ((String) values.get("plate_corners_gt")).split(" ") ;
				
				/*
				int x0 = Integer.valueOf(points[0]).intValue() - padding_left;
				if (x0 < 0) x0 = 0;
				
				int y0 = Integer.valueOf(points[1]).intValue() - padding_top;
				if (y0 < 0) y0 = 0;
				
				int x1 = Integer.valueOf(points[4]).intValue() + padding_right;
				if (x1 > imageBoofcv.getWidth()) x1 = imageBoofcv.getWidth();
				
				int y1 = Integer.valueOf(points[5]).intValue() + padding_bottom;
				if (y1 > imageBoofcv.getHeight()) y1 = imageBoofcv.getHeight();
				*/
				int x0=Integer.MAX_VALUE,x1=Integer.MIN_VALUE;
				for(int i=0; i < points.length; i=i+2) {
					int current = Integer.valueOf(points[i]).intValue();
					if (current < x0) x0 = current;
					if (current > x1) x1 = current;
				}
				if (x0 < 0) x0 = 0;
				//if (x1 > imageBoofcv.getWidth()) x1 = imageBoofcv.getWidth();
				if (x1 > image.size().width) x1 = (int) image.size().width;
				
				
				int y0=Integer.MAX_VALUE,y1=Integer.MIN_VALUE;
				for(int i=1; i < points.length; i=i+2) {
					int current = Integer.valueOf(points[i]).intValue();
					if (current < y0) y0 = current;
					if (current > y1) y1 = current;
				}
				if (y0 < 0) y0 = 0;
				//if (y1 > imageBoofcv.getHeight()) y1 = imageBoofcv.getHeight();
				if (y1 > image.size().height) y1 = (int) image.size().height;
				
				//GrayF32 plate = imageBoofcv.subimage(x0, y0, x1, y1);
				Mat plate = ocvw.recortarImagen(image, x0, y0, x1-x0, y1-y0);
				
				
				int plateWidth = x1-x0;
				int plateHeight = y1-y0;
				
				//System.out.println("plateWidth: " + plateWidth);
				//System.out.println("plateHeight: " + plateHeight);
				double ratio = plateWidth / plateHeight;
				if ((ratio > 0.9) && (ratio < 1.1)) quad = "_cuadrada";
				else quad = "";
				
				System.out.println("Fichero recortado: " + ((String)values.get("image_file")).replaceAll("\\.", suffix + quad + "\\."));
				
				//UtilImageIO.saveImage(plate, targetFolder + ((String)values.get("image_file")).replaceAll("\\.", suffix + quad + "\\."));
				ocvw.guardarImagen(plate, targetFolder + ((String)values.get("image_file")).replaceAll("\\.", suffix + quad + "\\."));
				
				counter++;
				System.out.println("OK");
				
			}
			
			System.out.println("Fin recorte");
			System.out.println("Total recortes: " + counter);
			
		} catch(Exception exc) {
			System.out.println("Error: " + exc.getMessage());
			System.out.println("Detalle: " + exc.toString());
		}
	}
	

}
