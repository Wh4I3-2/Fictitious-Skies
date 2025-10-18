#version 150

#moj_import <fog.glsl>

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec2 ScreenSize;
uniform vec4 FogColor;
uniform mat4 ProjMat;
uniform mat4 ModelViewMatrix;
uniform sampler2D Sampler0;
uniform vec2 View;
uniform float FOV;

in float vertexDistance;

out vec4 fragColor;

vec2 getUV(vec3 t3) {
    t3 = normalize(t3);
    vec3 q3 = abs(t3);

    bvec3 isX = greaterThanEqual(vec3(q3.x), q3.yzz);
    bvec3 isY = greaterThanEqual(vec3(q3.y), q3.xzz);
    bvec3 isZ = greaterThanEqual(vec3(q3.z), q3.xyy);

    float useX = float(isX.x && isX.y); // q3.x >= q3.y && q3.x >= q3.z
    float useY = float(isY.x && isY.y); // q3.y >= q3.x && q3.y >= q3.z
    float useZ = float(isZ.x && isZ.y); // q3.z >= q3.x && q3.z >= q3.y

    vec2 xUV = vec2(0.5 - t3.z / t3.x, 0.5 - t3.y / q3.x);
    vec2 yUV = vec2(0.5 + t3.x / q3.y, 0.5 + t3.z / t3.y);
    vec2 zUV = vec2(0.5 + t3.x / t3.z, 0.5 - t3.y / q3.z);

    return xUV * useX + yUV * useY + zUV * useZ;
}

vec2 getUVOffset(vec3 dir1) {
    dir1 = normalize(dir1);
    vec3 dir = abs(dir1);

    bool x = dir.x >= dir.y && dir.x >= dir.z;
    bool y = dir.y >= dir.x && dir.y >= dir.z;
    bool z = dir.z >= dir.x && dir.z >= dir.y;

    float isEast  = float(x && dir1.x > 0.0);
    float isWest  = float(x && dir1.x < 0.0);
    float isUp    = float(y && dir1.y > 0.0);
    float isDown  = float(y && dir1.y < 0.0);
    float isSouth = float(z && dir1.z > 0.0);
    float isNorth = float(z && dir1.z <= 0.0);

    return
        vec2(2.0, 1.0) * isEast +
        vec2(0.0, 1.0) * isWest +
        vec2(1.0, 0.0) * isUp +
        vec2(1.0, 2.0) * isDown +
        vec2(1.0, 1.0) * isSouth +
        vec2(3.0, 1.0) * isNorth;
}

const float PI = 3.14159265359;

mat3 camera(vec3 cameraPos, vec3 lookAtPoint) {
    vec3 cd = normalize(lookAtPoint - cameraPos);

    vec3 worldUp = vec3(0.0, 1.0, 0.0);
    if (abs(dot(cd, worldUp)) > 0.99) {
        worldUp = vec3(0.0, 0.0, 1.0);
    }

    vec3 cr = normalize(cross(worldUp, cd));
    vec3 cu = normalize(cross(cd, cr));

    return mat3(cr, cu, cd);
}

mat3 getViewMatrix(float pitch, float yaw) {
    float cp = cos(pitch);
    float sp = sin(pitch);
    float cy = cos(yaw);
    float sy = sin(yaw);

    vec3 forward = vec3(sy * cp, sp, cy * cp);
    vec3 right = vec3(cy, 0.0, -sy);
    vec3 up = cross(forward, right);

    return mat3(right, up, forward);
}

void main() {
    float fovRad = radians(FOV);
    float tanFov = tan(fovRad / 2.0);

    float aspect = ScreenSize.x / ScreenSize.y;

    vec2 uv = (gl_FragCoord.xy / ScreenSize.xy) * 2.0 - 1.0;

    uv.x *= aspect * tanFov;
    uv.y *= tanFov;

    float pitch = radians(View.x);
    float yaw = -radians(View.y);

    mat3 cam = getViewMatrix(pitch, yaw);
    vec3 rd = normalize(cam * vec3(uv, -1.0));

    fragColor = texture(Sampler0, (getUV(rd)/2.0+0.25+getUVOffset(rd))/4.0);
}