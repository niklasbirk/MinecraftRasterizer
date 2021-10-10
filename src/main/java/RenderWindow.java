import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class RenderWindow
{

    private final int GLFW_WINDOW_SIZE = 200;

    private long window;

    private final List<Map.Entry<Float, Float>> values;

    public RenderWindow(final List<Map.Entry<Float, Float>> values)
    {
        this.values = values;

        glfwInit();
    }

    private void destroy()
    {
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    private void glfwInit()
    {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!org.lwjgl.glfw.GLFW.glfwInit())
        {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        window = glfwCreateWindow(GLFW_WINDOW_SIZE, GLFW_WINDOW_SIZE, "Evaluation Rendering", NULL, NULL);
        if (window == NULL)
        {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
            {
                glfwSetWindowShouldClose(window, true);
            }
        });

        try (final MemoryStack stack = stackPush())
        {
            final IntBuffer pWidth = stack.mallocInt(1); // int*
            final IntBuffer pHeight = stack.mallocInt(1); // int*

            glfwGetWindowSize(window, pWidth, pHeight);

            final GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                    window,
                    (Objects.requireNonNull(vidmode).width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(window);

        glfwShowWindow(window);
    }

    public float[] render()
    {
        GL.createCapabilities();

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        glBegin(GL_LINE_STRIP);

        for (final Map.Entry<Float, Float> entry : this.values)
        {
            glVertex2f(entry.getKey(), entry.getValue());
        }

        glEnd();

        int pixelAmount = MainWindow.PIXEL_READ_SIZE * MainWindow.PIXEL_READ_SIZE;
        final float[] readPixels = new float[pixelAmount];
        int pixelReadRectangle = (GLFW_WINDOW_SIZE - MainWindow.PIXEL_READ_SIZE) / 2;
        glReadPixels(pixelReadRectangle, pixelReadRectangle, MainWindow.PIXEL_READ_SIZE, MainWindow.PIXEL_READ_SIZE, GL_RED, GL_FLOAT, readPixels);

        glfwPollEvents();

        this.destroy();

        float[] pixels = new float[pixelAmount];

        for (int i = 0; i < pixelAmount; i++)
        {
            final int row = i / MainWindow.PIXEL_READ_SIZE;
            final int col = i % MainWindow.PIXEL_READ_SIZE;
            final int index = (MainWindow.PIXEL_READ_SIZE - row - 1) * MainWindow.PIXEL_READ_SIZE + col;

            pixels[i] = readPixels[index];
        }

        return pixels;
    }
}
