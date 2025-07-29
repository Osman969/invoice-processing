# STEP 1: Build Vite app
FROM node:20 AS build
WORKDIR /app
COPY client/ .
RUN npm install && npm run build

# STEP 2: Serve with Nginx
FROM nginx:stable-alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY docker/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
