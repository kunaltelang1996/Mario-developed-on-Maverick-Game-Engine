#type vertex
        #version 330 core
        layout(location = 0) in vec3 aPos;
        layout(location = 1) in vec4 aColor;
        layout(location = 2) in vec2 aTexCoord;

        uniform mat4 uProjection;
        uniform mat4 uView;

        out vec4 fColor;
        out vec2 fTexCoord;
        void main()
        {
            fColor = aColor;
            fTexCoord = aTexCoord;
            gl_Position = uProjection * uView * vec4(aPos, 1.0);
        }


#type fragment
        #version 330 core

        uniform float uTime;
        uniform sampler2D tex_sampler;

        in vec2 fTexCoord;
        in vec4 fColor;
        out vec4 Color;

        void main()
        {
            //float avg = (fColor.r + fColor.g + fColor.b) / 3.0;
            //Color = vec4(avg, avg, avg, 1.0);
            //float noise = fract(sin(dot(fColor.rgb, vec3(12.9898, 78.233, 151.7182))) * 43758.5453);
            //Color = fColor * noise;
            //Color = vec4(fColor.r * noise, fColor.g * noise, fColor.b * noise, 1.0);
            //Color = fColor;
                Color = texture(tex_sampler, fTexCoord);
        }

