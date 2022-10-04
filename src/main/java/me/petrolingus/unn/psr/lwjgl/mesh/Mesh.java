package me.petrolingus.unn.psr.lwjgl.mesh;

import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33C;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Mesh {

    private final int vaoId;
    private final List<Integer> vboIdList;

    public Mesh(float[] positions, int[] indices) {

        this.vboIdList = new ArrayList<>();

        FloatBuffer positionsBuffer = null;
        IntBuffer indicesBuffer = null;
        try {

            this.vaoId = glGenVertexArrays();
            glBindVertexArray(this.vaoId);

            // Position VBO
            int vboId = glGenBuffers();
            vboIdList.add(vboId);
            positionsBuffer = MemoryUtil.memAllocFloat(positions.length);
            positionsBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STREAM_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            vboId = glGenBuffers();
            vboIdList.add(vboId);
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
            GL33C.glVertexAttribDivisor(1, 1);

            // Index VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {
            if (positionsBuffer != null) {
                MemoryUtil.memFree(positionsBuffer);
            }
            if (indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer);
            }
        }
    }

    public int getVaoId() {
        return vaoId;
    }

    public void drawInstances(int count) {
        glBindVertexArray(getVaoId());
        GL31.glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, count);
        glBindVertexArray(0);
    }

    public void bufferDataUpdate(FloatBuffer buffer) {
        glBindBuffer(GL_ARRAY_BUFFER, vboIdList.get(1));
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW);
    }
}
