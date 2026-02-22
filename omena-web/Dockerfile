# Build Angular
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# Serve with nginx
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
# Angular build output (Angular 17+): dist/<app-name>/browser
COPY --from=build /app/dist/front-auth/browser /usr/share/nginx/html
EXPOSE 80
