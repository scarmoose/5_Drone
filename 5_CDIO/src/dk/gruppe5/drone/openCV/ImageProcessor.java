package dk.gruppe5.drone.openCV;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ImageProcessor {
	
	public BufferedImage toBufferedImage(Mat matrix){
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if ( matrix.channels() > 1 ) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = matrix.channels()*matrix.cols()*matrix.rows();
		byte [] buffer = new byte[bufferSize];
		matrix.get(0,0,buffer); // get all the pixels
		BufferedImage image = new BufferedImage(matrix.cols(),matrix.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);  
		return image;
	}
	
	public Mat bufferedImageToMat(BufferedImage bi) {
		  Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
		  byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		  mat.put(0, 0, data);
		  return mat;
		}
	
	public Mat blur(Mat input, int numberOfTimes){
		Mat sourceImage = new Mat();
		Mat destImage = input.clone();
		for(int i=0;i<numberOfTimes;i++){
			sourceImage = destImage.clone();
			Imgproc.blur(sourceImage, destImage, new Size(3.0, 3.0));
		}
		return destImage;
	}
	
	public Mat erode(Mat input, int elementSize, int elementShape){
		Mat outputImage = new Mat();
		Mat element = getKernelFromShape(elementSize, elementShape);
		Imgproc.erode(input,outputImage, element);
		return outputImage;
	}

	

	public Mat dilate(Mat input, int elementSize, int elementShape) {
		Mat outputImage = new Mat();
		Mat element = getKernelFromShape(elementSize, elementShape);
		Imgproc.dilate(input,outputImage, element);
		return outputImage;
	}

	public Mat open(Mat input, int elementSize, int elementShape) {
		Mat outputImage = new Mat();
		Mat element = getKernelFromShape(elementSize, elementShape);
		Imgproc.morphologyEx(input,outputImage, Imgproc.MORPH_OPEN, element);
		return outputImage;
	}

	public Mat close(Mat input, int elementSize, int elementShape) {
		Mat outputImage = new Mat();
		Mat element = getKernelFromShape(elementSize, elementShape);
		Imgproc.morphologyEx(input,outputImage, Imgproc.MORPH_CLOSE, element);
		return outputImage;
	}
	
	private Mat getKernelFromShape(int elementSize, int elementShape) {
		return Imgproc.getStructuringElement(elementShape, new Size(elementSize*2+1, elementSize*2+1), new Point(elementSize, elementSize) );
	}
	
	public BufferedImage toGrayScale(BufferedImage input){
		
		byte[] data = ((DataBufferByte) input.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(input.getHeight(), input.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, data);

        Mat mat1 = new Mat(input.getHeight(),input.getWidth(),CvType.CV_8UC3);
        Imgproc.cvtColor(mat, mat1, Imgproc.COLOR_RGB2GRAY);

        byte[] data1 = new byte[mat1.rows() * mat1.cols() * (int)(mat1.elemSize())];
        mat1.get(0, 0, data1);
        BufferedImage image1 = new BufferedImage(mat1.cols(),mat1.rows(), BufferedImage.TYPE_BYTE_GRAY);
        image1.getRaster().setDataElements(0, 0, mat1.cols(), mat1.rows(), data1);

		
		
		return image1;
		
	}
	
	
	

	public Mat toCanny(Mat img) {
		//First we grayscale the picture
		Mat imageGray = new Mat();
		Mat imageCny = new Mat();
		Imgproc.cvtColor(img, imageGray, Imgproc.COLOR_BGR2GRAY);		
		Imgproc.Canny(imageGray, imageCny, 10, 100, 3, true);

	    
		return imageCny;
	}

	public Mat opticalFlow(Mat frameOne, Mat frameTwo) {
		// TODO Auto-generated method stub
		
	
		frameOne = toCanny(frameOne);
		return frameOne;
	}

}
